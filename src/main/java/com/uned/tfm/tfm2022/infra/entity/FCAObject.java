package com.uned.tfm.tfm2022.infra.entity;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Object")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FCAObject {
    @Id
    @NotNull
    private String name;
}
