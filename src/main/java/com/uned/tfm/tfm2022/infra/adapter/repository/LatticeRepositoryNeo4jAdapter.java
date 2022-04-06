package com.uned.tfm.tfm2022.infra.adapter.repository;

import com.jbraindead.core.formalEntities.ComplexEntity;
import com.jbraindead.core.formalEntities.FormalConcept;
import com.uned.tfm.tfm2022.infra.entity.FCAAttribute;
import com.uned.tfm.tfm2022.infra.entity.FCAObject;
import com.uned.tfm.tfm2022.infra.entity.FormalConceptDto;
import com.uned.tfm.tfm2022.infra.repository.FCARepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
public class LatticeRepositoryNeo4jAdapter implements com.uned.tfm.tfm2022.domain.repository.FCARepository {
    private final ModelMapper mapper = new ModelMapper();

    private final FCARepository fcaRepository;

    public LatticeRepositoryNeo4jAdapter(FCARepository fcaRepository) {
        this.fcaRepository = fcaRepository;
    }


    @Override
    public void saveAllNodes(List<FormalConcept> formalConcepts, String[] objects, String[] attributes) {

        List<FormalConcept> leafs = searchForLeafs(formalConcepts);

        log.info("Searching and saving bottom to up");

        saveLastLevel(leafs, Collections.emptyList(), objects, attributes);

        List<FormalConcept> unsaved;

        do {
            unsaved = checkIfAllAreSaved(formalConcepts);

            log.info("Retry saving unsaved nodes");

            unsaved.forEach(node -> saveUnsavedNode(node, objects, attributes));
        } while (!unsaved.isEmpty());

        log.info("Everything saved");
    }

    private void searchParents(List<FormalConcept> leafs, List<FormalConceptDto> savedLeafs, String[] objects, String[] attributes) {
        Set<FormalConcept> parents = leafs.stream().parallel()
                .flatMap(leaf -> leaf.getParents().stream())
                .collect(Collectors.toSet());

        if(parents.isEmpty())
            return;

        saveLastLevel(new ArrayList<>(parents), savedLeafs, objects, attributes);

    }

    private void saveLastLevel(List<FormalConcept> leafs, List<FormalConceptDto> savedLeafs, String[] objects, String[] attributes) {
        List<FormalConceptDto> leafToSave = leafs.stream().parallel()
                .map(leaf -> transformFcaToDto(leaf, savedLeafs, objects, attributes))
                .collect(Collectors.toList());

        List<FormalConceptDto> thisSavedLeafs = fcaRepository.saveAll(leafToSave);

        searchParents(leafs, thisSavedLeafs, objects, attributes);
    }


    private List<FormalConcept> searchForLeafs(List<FormalConcept> formalConcepts) {

        log.info("Get all nodes without children");

        return formalConcepts.stream().parallel()
                .filter(fca -> fca.getChildren().size() == 0)
                .collect(Collectors.toList());
    }

    private void saveUnsavedNode(FormalConcept unsavedNode, String[] objects, String[] attributes) {
        List<FormalConceptDto> parents = unsavedNode.getParents().stream().parallel().map(parent -> {
            Optional<FormalConceptDto> parentDtoOptional = fcaRepository.findById(parent.getId());

            FormalConceptDto parentDto = null;

            if (parentDtoOptional.isPresent()) {
                parentDto = parentDtoOptional.get();

                FormalConceptDto dto = mapper.map(unsavedNode, FormalConceptDto.class);

                FormalConceptDto rootDto = addExtensionAndIntension(dto, unsavedNode, objects, attributes);

                parentDto.getChildrenDto().add(rootDto);
            }

            return parentDto;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        fcaRepository.saveAll(parents);

    }

    private List<FormalConcept> checkIfAllAreSaved(List<FormalConcept> formalConcepts) {
        return formalConcepts.stream().parallel()
                .filter(fca -> !fcaRepository.existsById(fca.getId()))
                .collect(Collectors.toList());
    }

    private FormalConceptDto transformFcaToDto(FormalConcept fca, List<FormalConceptDto> savedLeafs, String[] objects, String[] attributes) {
        FormalConceptDto dto = mapper.map(fca, FormalConceptDto.class);

        List<FormalConceptDto> children = fca.getChildren().stream()
                .map(child -> {
                    Optional<FormalConceptDto> childDto = savedLeafs.stream().parallel()
                            .filter(savedLeaf -> savedLeaf.getId().equals(child.getId()))
                            .findFirst();

                    return childDto.orElseGet(() -> fcaRepository.findById(child.getId()).orElse(null));

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dto.setChildrenDto(children);

        addExtensionAndIntension(dto, fca, objects, attributes);

        return dto;
    }


    private FormalConceptDto addExtensionAndIntension(FormalConceptDto dto, FormalConcept fca, String[] objects, String[] attributes) {

        dto.setExtensionDto(processExtension(fca.getExtension(), objects));

        dto.setIntensionDto(processIntension(fca.getIntension(), attributes));

        return dto;
    }


    private List<FCAObject> processExtension(ComplexEntity extension, String[] objectList) {
        return complexToStringArray(objectList, extension).stream().parallel()
                .map(FCAObject::new)
                .collect(Collectors.toList());
    }

    private List<FCAAttribute> processIntension(ComplexEntity intension, String[] attributeList) {
        return complexToStringArray(attributeList, intension).stream().parallel()
                .map(FCAAttribute::new)
                .collect(Collectors.toList());
    }


    private List<String> complexToStringArray(String[] elems, ComplexEntity extension) {
        return extension.getElements().stream()
                .map(elem -> elems[elem.getId()])
                .collect(Collectors.toList());
    }


}
