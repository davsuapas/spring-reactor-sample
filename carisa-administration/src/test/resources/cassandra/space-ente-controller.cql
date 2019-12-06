DROP TABLE IF EXISTS carisa_dependency_relation
CREATE TABLE IF NOT EXISTS carisa_dependency_relation (parentId UUID, relation int, childId UUID, PRIMARY KEY ((parentId, relation), childId))
INSERT into carisa_dependency_relation(parentId, relation, childId) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 1, 7acdac69-fdf8-45e5-a189-2b2b4beb1c26)
INSERT into carisa_dependency_relation(parentId, relation, childId) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 1, c58698cd-bd52-4fd2-bcf6-6d54bcdc4069)
