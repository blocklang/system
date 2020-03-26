# `SYS_USER` - 用户信息

专门存储用户登录相关的信息

## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | varchar  | 32   |        | 是   | 否   |
| username            | 用户名         | varchar  | 64   |        |      | 否   |
| nickname            | 昵称           | varchar  | 64   |        |      | 是   |
| password            | 密码           | varchar  | 128  |        |      | 否   |
| sex                 | 性别           | char     | 1    |        |      | 是   |
| phone_number        | 手机号码       | varchar  | 11   |        |      | 是   |
| admin               | 是否管理员     | boolean  |      | false  |      | 否   |
| dept_id             | 部门标识       | varchar  | 32   |        |      | 是   |
| last_sign_in_time   | 最近登录时间   | datetime |      |        |      | 是   |
| sign_in_count       | 登录次数       | int      |      | 0      |      | 否   |
| create_user_id      | 创建人标识     | varchar  | 32   |        |      | 否   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | varchar  | 32   |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_USER`
* 外键：无
* 索引：`UK_USER_ON_USERNAME`，对应字段 `username`；`UK_USER_ON_PHONE_NUMBER`，对应字段 `phone_number`

## 说明

1. 用户是通过注册添加时，`create_user_id` 的值为注册用户的 `dbid`
2. `sex` 的值为：`1` 表示 `男`，`2` 表示 `女`
3. `admin` 是系统的超级管理员，第一个注册的用户为超级管理员
4. 注意，本表不能包含 `app_id`，主要是为了实现用户信息只在一处集中维护，但是可以通过为用户赋予不同 APP 下的角色来访问不同 APP 下的功能
5. 当用户的 `admin` 属性为 `true` 时，用户可以不归属于任何机构，可将 `dept_id` 的值设置为 `-1`
