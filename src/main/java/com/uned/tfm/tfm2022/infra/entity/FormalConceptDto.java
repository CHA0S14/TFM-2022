package com.uned.tfm.tfm2022.infra.entity;

import com.jbraindead.core.formalEntities.ComplexEntity;
import com.sun.istack.internal.NotNull;
import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Node("FormalConcept")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormalConceptDto {
    @Id
    private Integer id = -1;

    @Relationship(type = "EXTENSION", direction = Relationship.Direction.OUTGOING)
    private List<FCAObject> extensionDto;

    @Relationship(type = "INTENSION", direction = Relationship.Direction.OUTGOING)
    private List<FCAAttribute> intensionDto;

    @Relationship(type = "PARENT", direction = Relationship.Direction.OUTGOING)
    private List<FormalConceptDto> childrenDto;


    private BigDecimal intensionalStability;
    private BigDecimal subSets = new BigDecimal(0);
    private BigDecimal originalSubSets = new BigDecimal(0);
}
