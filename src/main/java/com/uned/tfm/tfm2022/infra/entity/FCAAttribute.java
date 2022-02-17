package com.uned.tfm.tfm2022.infra.entity;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Attribute")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class FCAAttribute {
    @Id
    @NotNull
    private String name;


    @Version
    private Long version;
}
