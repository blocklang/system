# `SYS_USER` - 用户信息

专门存储用户登录相关的信息

## 字段

| 字段名              | 注释           | 类型     | 长度 | 默认值 | 主键 | 可空 |
| ------------------- | -------------- | -------- | ---- | ------ | ---- | ---- |
| dbid                | 主键           | int      |      |        | 是   | 否   |
| login_name          | 登录名         | varchar  | 32   |        |      | 否   |
| nickname            | 昵称           | varchar  | 64   |        |      | 是   |
| password            | 密码           | varchar  | 128  |        |      | 否   |
| admin               | 是否管理员     | boolean  |      | false  |      | 否   |
| last_sign_in_time   | 最近登录时间   | datetime |      |        |      | 是   |
| create_user_id      | 创建人标识     | int      |      |        |      | 是   |
| create_time         | 创建时间       | datetime |      |        |      | 否   |
| last_update_user_id | 最近修改人标识 | int      |      |        |      | 是   |
| last_update_time    | 最近修改时间   | datetime |      |        |      | 是   |

## 约束

* 主键：`PK_SYS_USER`
* 外键：无
* 索引：`UK_LOGIN_NAME`，对应字段 `login_name`

## 说明

1. 用户是通过注册添加时，`create_user_id` 的值为空，表示是通过注册添加的用户
2. `admin` 是系统的超级管理员，第一个注册的用户为超级管理员
