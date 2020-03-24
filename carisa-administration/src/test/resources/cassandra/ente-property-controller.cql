DROP TABLE IF EXISTS carisa_ente_property
CREATE TABLE IF NOT EXISTS carisa_ente_property (parentId UUID, id UUID, name text, type int, PRIMARY KEY (parentId, id))
INSERT into carisa_ente_property(parentId, id, name, type) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, c0838415-6ae2-4914-b202-f1b3adbf0353, 'Ente property name', 1)
INSERT into carisa_ente_property(parentId, id, name, type) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, d0838415-6ae2-4914-b202-f1b3adbf0353, 'Ente property updated', 2)
INSERT into carisa_ente_property(parentId, id, name, type) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, e0838415-6ae2-4914-b202-f1b3adbf0353, 'Ente property name', 1)