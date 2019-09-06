DROP TABLE IF EXISTS carisa_space
CREATE TABLE IF NOT EXISTS carisa_space (id UUID, instanceId UUID, name text, PRIMARY KEY (id))
INSERT into carisa_space(id, instanceId, name) values (52107f03-cf1b-4760-b2c2-4273482f0f7a, 5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Space name')
INSERT into carisa_space(id, instanceId, name) values (12107f03-cf1b-4760-b2c2-4273482f0f7a, 5b6962dd-3f90-4c93-8f61-eabfa4a803e2, 'Space updated')