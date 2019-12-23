DROP TABLE IF EXISTS carisa_instance_space
CREATE TABLE IF NOT EXISTS carisa_instance_space (parentId UUID, spaceId UUID, PRIMARY KEY (parentId, spaceId))
INSERT into carisa_instance_space(parentId, spaceId) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 52107f03-cf1b-4760-b2c2-4273482f0f7a)
INSERT into carisa_instance_space(parentId, spaceId) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 575f260c-32f1-488b-99dc-7694db3eceee)
