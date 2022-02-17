package com.uned.tfm.tfm2022.infra.adapter.repository;

import com.jbraindead.core.formalEntities.ComplexEntity;
import com.jbraindead.core.formalEntities.FormalConcept;
import com.uned.tfm.tfm2022.infra.entity.FCAAttribute;
import com.uned.tfm.tfm2022.infra.entity.FCAObject;
import com.uned.tfm.tfm2022.infra.entity.FormalConceptDto;
import com.uned.tfm.tfm2022.infra.repository.FCARepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
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
        FormalConcept root = searchForRoot(formalConcepts);

        FormalConceptDto rootDto = mapper.map(root, FormalConceptDto.class);

        log.info("Processing children");

        rootDto.setChildrenDto(processChildren(root, objects, attributes));

        log.info("Saving");

        fcaRepository.save(rootDto);

        List<FormalConcept> unsaved;

        do {
            unsaved = checkIfAllAreSaved(formalConcepts);

            log.info("Retry saving unsaved nodes");

            unsaved.forEach(node -> saveUnsavedNode(node, objects, attributes));
        }while (!unsaved.isEmpty());

        log.info("Everything saved");
    }

    private void saveUnsavedNode(FormalConcept unsavedNode, String[] objects, String[] attributes) {
        List<FormalConceptDto> parents = unsavedNode.getParents().stream().map(parent -> {
            Optional<FormalConceptDto> parentDtoOptional = fcaRepository.findById(parent.getId());

            FormalConceptDto parentDto = null;

            if (parentDtoOptional.isPresent()) {
                parentDto = parentDtoOptional.get();


                FormalConceptDto rootDto = mapper.map(unsavedNode, FormalConceptDto.class);
                rootDto.setChildrenDto(processChildren(unsavedNode, objects, attributes));

                parentDto.getChildrenDto().add(rootDto);
            }

            return parentDto;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        fcaRepository.saveAll(parents);

    }

    private List<FormalConcept> checkIfAllAreSaved(List<FormalConcept> formalConcepts) {
        return formalConcepts.stream().parallel()
                .filter(fca -> !fcaRepository.findById(fca.getId()).isPresent())
                .collect(Collectors.toList());
    }


    private FormalConcept searchForRoot(List<FormalConcept> formalConcepts) {

        log.info("Get all nodes without parents");

        return formalConcepts.stream().parallel()
                .filter(fca -> fca.getParents().size() == 0)
                .findFirst().orElse(null);

    }

    private List<FormalConceptDto> processChildren(FormalConcept parent, String[] objects, String[] attributes) {

        return parent.getChildren().stream().map(child -> transformFcaToDto(child, objects, attributes)).collect(Collectors.toList());
    }

    private FormalConceptDto transformFcaToDto(FormalConcept fca, String[] objects, String[] attributes) {
        FormalConceptDto dto = mapper.map(fca, FormalConceptDto.class);

        List<FormalConceptDto> children = processChildren(fca, objects, attributes);

        dto.setChildrenDto(children);

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
