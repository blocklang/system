-- dev db
CREATE DATABASE system
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE system
    IS '系统管理开发数据库';


-- test db
CREATE DATABASE system_test
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE system_test
    IS '系统管理测试数据库';