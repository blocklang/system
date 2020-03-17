# REST API

输入参数分三种：

1. path，如 `a/{b}/c`
2. queryParam, 如 `a?b={b}`
3. body, 如 POST 或 PUT 提交时传入的 JSON 数据

关键字：

1. TBD = to be determined

## 用户登录

1. [注册用户](./user/register.md)
2. [用户登录](./user/login.md)
3. [校验用户名](./user/check-username.md)

## 用户管理

不能提供删除用户的操作，最多让用户失效。

1. [获取用户列表](./users/list-users.md)
2. [获取一个用户信息](./users/get-a-user.md)
3. [创建一个用户](./users/create-a-user.md)
4. [更新一个用户](./users/update-a-user.md)

## APP 管理

1. [创建 APP](./app/create-a-app.md)
2. [获取一个 APP 信息](./app/get-a-app.md)