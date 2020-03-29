DROP TABLE IF EXISTS carisa_dynamic_object_prototype
CREATE TABLE IF NOT EXISTS carisa_dynamic_object_prototype (parentId UUID, id UUID, name text, PRIMARY KEY (id))
INSERT into carisa_dynamic_object_prototype(parentId, id, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, a985074c-796b-4ecb-9a8f-21f4b26aa11b, 'Query type name')
INSERT into carisa_dynamic_object_prototype(parentId, id, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, b985074c-796b-4ecb-9a8f-21f4b26aa11b, 'Query type updated')