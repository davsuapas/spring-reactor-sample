CREATE KEYSPACE IF NOT EXISTS carisa_administration WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

DROP TABLE IF EXISTS carisa_administration.carisa_instance;

CREATE TABLE IF NOT EXISTS carisa_administration.carisa_instance (
    id UUID,
    name text,
    PRIMARY KEY (id)
);

INSERT into carisa_administration.carisa_instance(id, name) values (5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Instance name');
INSERT into carisa_administration.carisa_instance(id, name) values (9b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Instance name');
