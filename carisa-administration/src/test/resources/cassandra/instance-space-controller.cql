DROP TABLE IF EXISTS carisa_dependency_relation
CREATE TABLE IF NOT EXISTS carisa_dependency_relation (parentId UUID, childId UUID, PRIMARY KEY (parentId, childId))
INSERT into carisa_dependency_relation(parentId, childId) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 52107f03-cf1b-4760-b2c2-4273482f0f7a)
INSERT into carisa_dependency_relation(parentId, childId) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 575f260c-32f1-488b-99dc-7694db3eceee)
