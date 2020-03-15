# `SYS_USER_ROLE` - 用户和角色关联表

## 字段

| 字段名         | 注释       | 类型     | 长度 | 默认值 | 主键 | 可空 |
| -------------- | ---------- | -------- | ---- | ------ | ---- | ---- |
| dbid           | 主键       | varchar  | 32   |        | 是   | 否   |
| user_id        | 用户标识   | varchar  | 32   |        |      | 否   |
| role_id        | 角色标识   | varchar  | 32   |        |      | 否   |
| create_user_id | 创建人标识 | varchar  | 32   |        |      | 否   |
| create_time    | 创建时间   | datetime |      |        |      | 否   |

## 约束

* 主键：`PK_SYS_USER_ROLE`
* 外键：无
* 索引：`UK_USER_ROLE_ON_USER_ROLE`(唯一索引)，对应字段 `user_id` 和 `role_id`

## 说明

1. 用户与角色是 N : N 关系
