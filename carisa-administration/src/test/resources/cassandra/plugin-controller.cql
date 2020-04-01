DROP TABLE IF EXISTS carisa_plugin
CREATE TABLE IF NOT EXISTS carisa_plugin (parentId UUID, childId UUID, PRIMARY KEY (parentId, childId))
