DROP TABLE IF EXISTS carisa_space_query_prototype
CREATE TABLE IF NOT EXISTS carisa_space_query_prototype (parentId UUID, childId UUID, PRIMARY KEY (parentId, childId))
INSERT into carisa_space_query_prototype(parentId, childId) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, a985074c-796b-4ecb-9a8f-21f4b26aa11b)