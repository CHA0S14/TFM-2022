package com.uned.tfm.tfm2022.infra.entity;

import com.jbraindead.core.formalEntities.ComplexEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Node("FormalConcept")
@Data
public class FormalConceptDto {
    @Id
    private Integer id = -1;

    @Relationship(type = "EXTENSION", direction = Relationship.Direction.OUTGOING)
    private List<FCAObject> extension;

    @Relationship(type = "INTENSION", direction = Relationship.Direction.OUTGOING)
    private List<FCAAttribute> intension;

    @Relationship(type = "PARENT", direction = Relationship.Direction.INCOMING)
    private ArrayList<FormalConceptDto> parents = new ArrayList<>();

    private BigDecimal intensionalStability;
    private BigDecimal subSets = new BigDecimal(0);
    private BigDecimal originalSubSets = new BigDecimal(0);


    public void setExtension(ComplexEntity extension, String[] objectList) {
        this.extension = conplexToStringArray(objectList, extension).stream()
                .map(FCAObject::new)
                .collect(Collectors.toList());
    }

    public void setIntension(ComplexEntity intension, String[] objectList) {
        this.intension = conplexToStringArray(objectList, intension).stream()
                .map(FCAAttribute::new)
                .collect(Collectors.toList());
    }


    private List<String> conplexToStringArray(String[] elems, ComplexEntity extension) {
        return extension.getElements().stream()
                .map(elem -> elems[elem.getId()])
                .collect(Collectors.toList());
    }
}
