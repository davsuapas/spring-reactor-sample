DROP TABLE IF EXISTS carisa_dynamic_object_prototype
CREATE TABLE IF NOT EXISTS carisa_dynamic_object_prototype (parentId UUID, id UUID, name text, description text, PRIMARY KEY (id))
INSERT into carisa_dynamic_object_prototype(parentId, id, name, description) values (9b3d108e-89cb-4c7c-a774-a527a8b22e57, 5d191729-1f4c-4b7e-b573-b90cf3457df8, 'Query prototype name', 'Query prototype description')
INSERT into carisa_dynamic_object_prototype(parentId, id,  name, description) values (9b3d108e-89cb-4c7c-a774-a527a8b22e57, 4d191729-1f4c-4b7e-b573-b90cf3457df8, 'Updated name', 'Updated description')