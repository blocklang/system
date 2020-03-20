# `SYS_DEPT` - 部门信息


## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | varchar  | 32   |        | 是   | 否   |
| dept_name           | 部门名称       | varchar  | 64   |        |      | 否   |
| parent_id           | 父部门标识     | varchar  | 32   | -1     |      | 否   |
| seq                 | 显示顺序       | int      |      | 1      |      | 否   |
| active              | 是否启用       | boolean  |      | true   |      | 否   |
| create_user_id      | 创建人标识     | varchar  | 32   |        |      | 否   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | varchar  | 32   |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_DEPT`
* 外键：无
* 索引：`UK_DEPT_ON_NAME_PARENT`(唯一索引)，对应字段 `dept_name` 和 `parent_id`

## 说明

1. `parent_id` 的值为 `-1` 时，表示顶级节点
2. `seq` 是相对于父部门的显示顺序，都是从 1 开始
