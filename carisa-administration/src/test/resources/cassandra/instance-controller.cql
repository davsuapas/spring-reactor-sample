CREATE KEYSPACE IF NOT EXISTS carisa_administration WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

DROP TABLE IF EXISTS carisa_administration.carisa_instance;

CREATE TABLE IF NOT EXISTS carisa_administration.carisa_instance (
    id UUID,
    name text,
    state int,
    PRIMARY KEY (id)
);

INSERT into carisa_administration.carisa_instance(id, name, state) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Instance name', 0);
INSERT into carisa_administration.carisa_instance(id, name, state) values (9b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Instance name', 0);
