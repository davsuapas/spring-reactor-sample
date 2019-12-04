DROP TABLE IF EXISTS carisa_dependency_relation
CREATE TABLE IF NOT EXISTS carisa_dependency_relation (parentId UUID, childId UUID, PRIMARY KEY (parentId, childId))
INSERT into carisa_dependency_relation(parentId, childId) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 7acdac69-fdf8-45e5-a189-2b2b4beb1c26)
INSERT into carisa_dependency_relation(parentId, childId) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, c58698cd-bd52-4fd2-bcf6-6d54bcdc4069)
