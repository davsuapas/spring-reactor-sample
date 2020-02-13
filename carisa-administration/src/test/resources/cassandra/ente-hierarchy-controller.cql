DROP TABLE IF EXISTS carisa_ente_hierarchy
CREATE TABLE IF NOT EXISTS carisa_ente_hierarchy (parentId UUID, id UUID, category boolean, PRIMARY KEY (parentId, id))
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 53ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, true)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 8acdac69-fdf8-45e5-a189-2b2b4beb1c24, true)
