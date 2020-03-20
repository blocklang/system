# 数据库

## 数据库表

1. [SYS_USER](SYS_USER.md)
2. [SYS_APP](SYS_APP.md)
3. [SYS_RESOURCE](SYS_RESOURCE.md)
4. [SYS_ROLE](SYS_ROLE.md)
5. [SYS_USER_ROLE](SYS_USER_ROLE.md)
6. [SYS_AUTH](SYS_AUTH.md)
7. [SYS_DEPT](SYS_DEPT.md)

## 默认数据

约定：

1. 如果是系统启动时初始化的数据，则使用到用户标识的地方统一使用 `-1`
2. 初始化数据的 `id` 要遵循特定的编码规则，这样不仅直观，也能留出空间，让后来者使用：
   1. 资源管理的 `id`，以两位为一段，每段都从 `01` 开始，段数不限制（这样支持任意层级），如 `01`，`0101` 等