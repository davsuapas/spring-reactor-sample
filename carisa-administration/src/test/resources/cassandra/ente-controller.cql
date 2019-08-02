CREATE KEYSPACE IF NOT EXISTS carisa_administration WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

DROP TABLE IF EXISTS carisa_administration.carisa_ente;

CREATE TABLE IF NOT EXISTS carisa_administration.carisa_ente (
    id UUID,
    spaceId UUID,
    name text,
    PRIMARY KEY (id)
);

INSERT into carisa_administration.carisa_ente(id, spaceId, name) values (7acdac69-fdf8-45e5-a189-2b2b4beb1c26, 52107f03-cf1b-4760-b2c2-4273482f0f7a, 'Ente name');
INSERT into carisa_administration.carisa_ente(id, spaceId, name) values (8acdac69-fdf8-45e5-a189-2b2b4beb1c26, 52107f03-cf1b-4760-b2c2-4273482f0f7a, 'Ente updated');