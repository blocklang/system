# 在角色中添加一个用户

校验规则：

1. 用户已登录
2. 用户有权访问
3. 不能重复添加

```text
POST /roles/{roleId}/users/{userId}/assign
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `roleId`(path)          | `string` | **Required**. 角色标识         |
| `userId`(path)          | `string` | **Required**. 用户标识         |

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

用户无权访问

```text
Status: 403 FORBIDDEN
```

如果已存在或添加成功

```text
Status: 200 OK
```
返回一个 JSON 对象，其中包括用户信息

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 用户标识         |
| `username`         | `string`  | 用户名           |
| `nickname`         | `string`  | 昵称             |
| `sex`              | `string`  | 性别             |
| `phoneNumber`      | `string`  | 手机号码         |
| `deptId`           | `string`  | 部门标识         |
| `deptName`         | `string`  | 部门名称         |
