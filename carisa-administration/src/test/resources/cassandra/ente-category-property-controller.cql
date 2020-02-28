DROP TABLE IF EXISTS carisa_ente_category_property
CREATE TABLE IF NOT EXISTS carisa_ente_category_property (parentId UUID, id UUID, name text, PRIMARY KEY (parentId, id))
INSERT into carisa_ente_category_property(parentId, id, name) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, b9439ad1-4419-4765-acca-55ce69179c0f, 'Ente category property name')
INSERT into carisa_ente_category_property(parentId, id, name) values (83ed3c4c-5c7f-4e76-8a2a-2e3b7bfca676, c9439ad1-4419-4765-acca-55ce69179c0f, 'Ente category property updated')