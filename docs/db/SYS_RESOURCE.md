# `SYS_RESOURCE` - 系统资源

系统资源表中主要存储三大类信息，分别是:

1. 功能模块：也称为目录，是对资源进行分组
2. 程序模块：也称为菜单，对应程序界面，一个界面由一个主界面和多个子界面组成
3. 操作按钮：用于控制界面上的按钮是否可见，以及按钮对应的后台 API 是否有权访问

按钮又分两种：

1. 一种是用来控制页面跳转的，如果没有跳转到某个子页面的权限，则不显示此按钮
2. 一种是用来控制 API 调用的，如果用户没有此类权限，则界面上将此按钮失效或隐藏，服务器端禁止调用 API

有些页面设计中，数据的增删改查都在一个页面呈现，即不存在子页面跳转的情况；而有时存在子页面跳转。这两种情况都只需要配置一个权限即可。如用户不具有新增机构的权限，则使用新增机构权限同时控制不能跳转到新增机构的子页面，同时也不能点击新增机构子页面中的按钮，同时也不能调用数据处理 API。


其中功能模块下可再分子功能模块，程序模块下可再包含子程序模块。

## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | varchar  | 32   |        | 是   | 否   |
| app_id              | APP 标识       | varchar  | 32   |        |      | 否   |
| res_name            | 资源名称       | varchar  | 64   |        |      | 否   |
| parent_id           | 父资源标识     | varchar  | 32   | -1     |      | 否   |
| seq                 | 显示顺序       | int      |      | 1      |      | 否   |
| url                 | 访问路径       | varchar  | 255  |        |      | 是   |
| icon                | 资源图标       | varchar  | 64   |        |      | 是   |
| resource_type       | 资源类型       | char     | 2    |        |      | 否   |
| description         | 资源描述       | text     |      |        |      | 是   |
| active              | 是否启用       | boolean  |      | true   |      | 否   |
| auth                | 权限标识       | varchar  | 64   |        |      | 是   |
| create_user_id      | 创建人标识     | varchar  | 32   |        |      | 否   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | varchar  | 32   |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_RESOURCE`
* 外键：无
* 索引：`UK_RES_ON_NAME_APP_PARENT`(唯一索引)，对应字段 `app_id`、`res_name` 和 `parent_id`

## 说明

1. `app_id` 表示为每个 APP 配置自己的资源
2. `parent_id` 的值为 `-1` 时，表示顶级节点
3. `seq` 是相对于父资源的显示顺序，都是从 1 开始
4. `url` 的值可以是外部连接，也可以是路由
5. `resource_type` 的值为：`01` 表示 `功能模块`，`02` 表示 `程序模块`，`03` 表示 `操作按钮`
6. `auth` 的值要遵循一套约定，如 `list` 表示 `查多条记录`，`query` 表示 `查询单条记录`，`new` 表示 `新增`，`edit` 表示 `修改`，`remove` 表示 `删除` 等等
7. `resource_type` 的值为 `03`(操作按钮)时，所有记录的 `auth` 值必须不同，即为不同的操作设置不同的 `auth`。这就是方法级别的操作权限。
8. 没有在 `app_id`、`auth` 和 `parent_id` 上添加唯一索引，是因为当 `resource_type` 的值不为 `03` 时不存在此唯一性约束