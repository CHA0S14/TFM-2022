package com.uned.tfm.tfm2022.domain.repository;

import com.jbraindead.core.formalEntities.FormalConcept;
import com.jbraindead.core.formalEntities.Lattice;

import java.util.List;

public interface FCARepository {
    void saveAllNodes(List<FormalConcept> lattice, String[] objects, String[] attributes);
}
