package com.uned.tfm.tfm2022.infra.repository;

import com.uned.tfm.tfm2022.infra.entity.FCAObject;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ObjectRepository extends Neo4jRepository<FCAObject, String> {
}
