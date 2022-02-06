package com.uned.tfm.tfm2022.infra.adapter.repository;

import com.jbraindead.core.formalEntities.FormalConcept;
import com.jbraindead.core.formalEntities.Lattice;
import com.uned.tfm.tfm2022.domain.repository.FCARepository;
import com.uned.tfm.tfm2022.infra.entity.FormalConceptDto;
import com.uned.tfm.tfm2022.infra.repository.LatticeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LatticeRepositoryNeo4jAdapter implements FCARepository {
    private final ModelMapper mapper = new ModelMapper();

    private final LatticeRepository repository;

    public LatticeRepositoryNeo4jAdapter(LatticeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveNode(Lattice lattice, FormalConcept fc) {
        FormalConceptDto formalConcept = mapper.map(fc, FormalConceptDto.class);

        repository.save(formalConcept);
    }

    @Override
    public void saveAllNodes(List<FormalConcept> formalConcepts) {

        List<FormalConceptDto> formalConceptList = formalConcepts.stream().parallel()
                .map(formalConcept -> mapper.map(formalConcept, FormalConceptDto.class))
                .collect(Collectors.toList());

        repository.saveAll(formalConceptList);
    }


}
