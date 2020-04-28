DROP TABLE IF EXISTS carisa_dynamic_object_instance_property
CREATE TABLE IF NOT EXISTS carisa_dynamic_object_instance_property (parentId UUID, id UUID, value text, PRIMARY KEY (parentId, id))
INSERT into carisa_dynamic_object_instance_property(parentId, id, value) values (a985074c-796b-4ecb-9a8f-21f4b26aa11b, c6b34eb0-e15e-4e5a-a20d-7548a6967085, '0;1')
INSERT into carisa_dynamic_object_instance_property(parentId, id, value) values (a985074c-796b-4ecb-9a8f-21f4b26aa11b, d6b34eb0-e15e-4e5a-a20d-7548a6967085, '0;1')