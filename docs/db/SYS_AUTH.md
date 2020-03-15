# `SYS_AUTH` - 授权信息，即角色与资源关联表

## 字段

| 字段名         | 注释       | 类型     | 长度 | 默认值 | 主键 | 可空 |
| -------------- | ---------- | -------- | ---- | ------ | ---- | ---- |
| dbid           | 主键       | varchar  | 32   |        | 是   | 否   |
| role_id        | 角色标识   | varchar  | 32   |        |      | 否   |
| resource_id    | 资源标识   | varchar  | 32   |        |      | 否   |
| app_id         | APP 标识   | varchar  | 32   |        |      | 否   |
| create_user_id | 创建人标识 | varchar  | 32   |        |      | 否   |
| create_time    | 创建时间   | datetime |      |        |      | 否   |

## 约束

* 主键：`PK_SYS_AUTH`
* 外键：无
* 索引：`UK_AUTH_ON_ROLE_RES`(唯一索引)，对应字段 `role_id` 和 `resource_id`

## 说明

1. 资源与角色是 N : N 关系
2. 注意：只能在同一个 APP 下的角色和资源之间建立关系，所以 `app_id` 是冗余字段，是为了明确标识出是哪个 APP 下的授权信息
