# REST API

输入参数分四种：

1. path - 如 `my/{path}` 中的 `path`
2. queryParam - 如 `my/path?query={query}` 中的 `query`
3. body - POST 或 PUT 提交时传入的 JSON 数据
4. header - 存储在 http request 中的 header 中
   1. Authorization 的格式为 `Token {tokenValue}`

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

1. [分页获取 APP 列表](./apps/list-apps.md)
3. [获取一个 APP 信息](./apps/get-a-app.md)
4. [创建一个 APP](./apps/create-a-app.md)
5. [更新一个 APP](./apps/update-a-app.md)

## 角色管理

1. [获取 APP 的角色列表](./roles/list-role-roles.md)
2. [获取一个角色信息](./roles/get-a-role.md)
3. [创建一个角色](./roles/create-a-role.md)
4. [更新一个角色](./roles/update-a-role.md)
5. [获取为一个角色分配的用户列表](./roles/list-role-users.md)
6. [在角色中添加一个用户](./roles/add-user-to-role.md)
7. [在角色中删除一个用户](./roles/remove-user-from-role.md)
8. [获取为一个角色分配的资源列表](./roles/list-role-resources.md)
9. [批量保存 APP 下一个角色的所有授权的资源](./roles/add-app-role-resources.md)

## 资源管理

1. [获取 APP 某个资源的直属子资源列表](./resources/list-app-child-resources.md)
2. [获取一个资源](./resources/get-a-resource.md)
3. [创建一个资源](./resources/create-a-resource.md)
4. [更新一个资源](./resources/update-a-resource.md)
5. [获取登录用户对资源的权限列表](./resources/list-user-resource-permissions.md)
6. [获取登录用户有权访问的资源列表](./resources/list-user-resources.md)

## 部门管理

1. [获取某个部门的直属子部门列表](./depts/list-child-depts.md)
2. [获取一个部门](./depts/get-a-dept.md)
3. [创建一个部门](./depts/create-a-dept.md)
4. [更新一个部门](./depts/update-a-dept.md)