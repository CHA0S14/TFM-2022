package com.uned.tfm.tfm2022.infra.repository;

import com.uned.tfm.tfm2022.infra.entity.FormalConceptDto;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface LatticeRepository extends Neo4jRepository<FormalConceptDto, Integer> {
}
