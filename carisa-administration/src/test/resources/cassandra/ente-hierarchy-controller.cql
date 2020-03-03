DROP TABLE IF EXISTS carisa_ente_hierarchy
CREATE TABLE IF NOT EXISTS carisa_ente_hierarchy (parentId UUID, id UUID, category boolean, PRIMARY KEY (parentId, id))
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 53ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, true)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 8acdac69-fdf8-45e5-a189-2b2b4beb1c24, true)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 7acdac69-fdf8-45e5-a189-2b2b4beb1c26, false)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, 0579317c-1c0c-4924-8f4b-8d0749ef4165, false)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, true)
INSERT into carisa_ente_hierarchy(parentId, id, category) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 96c5425f-56b2-49fc-a9bd-20e7a09eede2, true)
