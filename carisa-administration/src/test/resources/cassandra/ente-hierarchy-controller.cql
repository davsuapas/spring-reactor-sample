DROP TABLE IF EXISTS carisa_ente_hierarchy
CREATE TABLE IF NOT EXISTS carisa_ente_hierarchy (parentId UUID, id UUID, category boolean, PRIMARY KEY (parentId, id))

