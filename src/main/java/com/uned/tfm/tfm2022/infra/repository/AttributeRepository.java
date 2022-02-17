package com.uned.tfm.tfm2022.infra.repository;

import com.uned.tfm.tfm2022.infra.entity.FCAAttribute;
import com.uned.tfm.tfm2022.infra.entity.FCAObject;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AttributeRepository extends Neo4jRepository<FCAAttribute, String> {
}
