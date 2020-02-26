DROP TABLE IF EXISTS carisa_ente
CREATE TABLE IF NOT EXISTS carisa_ente (spaceId UUID, id UUID, name text, PRIMARY KEY (id))
INSERT into carisa_ente(spaceId, id, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 7acdac69-fdf8-45e5-a189-2b2b4beb1c26, 'Ente name')
INSERT into carisa_ente(spaceId, id, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 8acdac69-fdf8-45e5-a189-2b2b4beb1c26, 'Ente updated')