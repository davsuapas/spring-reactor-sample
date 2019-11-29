DROP TABLE IF EXISTS carisa_ente_property
CREATE TABLE IF NOT EXISTS carisa_ente_property (enteId UUID, propertyId UUID, name text, type int, PRIMARY KEY (enteId, propertyId))
INSERT into carisa_ente_property(enteId, propertyId, name, type) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, c0838415-6ae2-4914-b202-f1b3adbf0353, 'Ente property name', 0)
INSERT into carisa_ente_property(enteId, propertyId, name, type) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, d0838415-6ae2-4914-b202-f1b3adbf0353, 'Ente property updated', 1)