# `SYS_APP` - APP 信息

本后台管理系统支持管理多个 APP。

## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | varchar  | 32   |        | 是   | 否   |
| app_name            | app 名称       | varchar  | 64   |        |      | 否   |
| url                 | app 访问地址   | varchar  | 64   |        |      | 是   |
| icon                | app logo       | varchar  | 64   |        |      | 是   |
| seq                 | 排序           | int      |      |        |      | 否   |
| description         | app 描述       | text     |      |        |      | 是   |
| active              | 是否启用       | boolean  |      | true   |      | 否   |
| create_user_id      | 创建人标识     | varchar  | 32   |        |      | 否   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | varchar  | 32   |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_APP`
* 外键：无
* 索引：`UK_APP_ON_NAME_USER`(唯一索引)，对应字段 `app_name` 和 `create_user_id`

## 说明

1. `url` 的值默认为空字符串
2. `seq` 专用于排列某个用户创建的项目，从 1 开始
