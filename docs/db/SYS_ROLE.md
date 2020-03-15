# `SYS_ROLE` - 角色信息表

## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | varchar  | 32   |        | 是   | 否   |
| app_id              | APP 标识       | varchar  | 32   |        |      | 否   |
| role_name           | 角色名称       | varchar  | 64   |        |      | 否   |
| description         | 角色描述       | text     |      |        |      | 是   |
| seq                 | 显示顺序       | int      |      | 1      |      | 否   |
| active              | 是否启用       | boolean  |      | true   |      | 否   |
| create_user_id      | 创建人标识     | varchar  | 32   |        |      | 否   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | varchar  | 32   |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_ROLE`
* 外键：无
* 索引：`UK_ROLE_ON_APP_NAME`(唯一索引)，对应字段 `app_id` 和 `role_name`

## 说明

1. `app_id` 表示为每个 APP 单独定义一套角色
2. `seq` 是在 `app_id` 一级排序的，从 1 开始
