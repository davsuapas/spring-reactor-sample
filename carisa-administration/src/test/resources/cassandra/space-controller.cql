CREATE KEYSPACE IF NOT EXISTS carisa_administration WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

DROP TABLE IF EXISTS carisa_administration.carisa_space;

CREATE TABLE IF NOT EXISTS carisa_administration.carisa_space (
    id UUID,
    instanceId UUID,
    name text,
    PRIMARY KEY (id)
);

INSERT into carisa_administration.carisa_space(id, instanceId, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 523170e2-cd49-4261-913b-1a4c0c7652cf, 'Space name');
INSERT into carisa_administration.carisa_space(id, instanceId, name) values (12107f03-cf1b-4760-b2c2-4273482f0f7a, 523170e2-cd49-4261-913b-1a4c0c7652cf, 'Space updated');