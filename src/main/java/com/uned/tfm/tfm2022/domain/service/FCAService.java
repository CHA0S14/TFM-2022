package com.uned.tfm.tfm2022.domain.service;

import com.jbraindead.core.formalEntities.Lattice;
import com.uned.tfm.tfm2022.domain.repository.FCARepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class FCAService {

    private final FCARepository repository;

    public FCAService(FCARepository repository) {
        this.repository = repository;
    }

    public void saveLattice(Lattice lattice) {

        repository.saveAllNodes(lattice.getLattice());
    }
}
