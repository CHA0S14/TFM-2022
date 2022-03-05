from neomodel import StructuredNode, StringProperty, IntegerProperty, RelationshipTo, RelationshipFrom, config


def connect(url):
    config.DATABASE_URL = url


class FormalConcept(StructuredNode):
    @classmethod
    def category(cls):
        pass

    id = IntegerProperty(unique_index=True, required=True)
    intension = RelationshipTo('Attribute', 'INTENSION')
    extension = RelationshipTo('Object', 'EXTENSION')


class Attribute(StructuredNode):
    @classmethod
    def category(cls):
        pass

    name = StringProperty(unique_index=True, required=True)
    formal_concepts = RelationshipFrom('FormalConcept', 'INTENSION')

    def formal_concepts_with_attr(self):
        results, columns = self.cypher("MATCH (a) WHERE id(a)={self} MATCH (a)-[:INTENSION]->(b) RETURN b")
        return [FormalConcept.inflate(row[0]) for row in results]


class Object(StructuredNode):
    @classmethod
    def category(cls):
        pass

    name = StringProperty(unique_index=True, required=True)
    formal_concepts = RelationshipFrom('FormalConcept', 'EXTENSION')
