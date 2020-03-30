# 获取为一个角色分配的用户列表

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /roles/{roleId}/users
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `roleId`(path)          | `string` | **Required**. 角色标识         |

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

用户无权访问

```text
Status: 403 FORBIDDEN
```

获取成功

```text
Status: 200 OK
```

返回一个 JSON 数组，其中包含用户信息列表

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 用户标识         |
| `username`         | `string`  | 用户名           |
| `nickname`         | `string`  | 昵称             |
| `sex`              | `string`  | 性别             |
| `phoneNumber`      | `string`  | 手机号码         |
| `deptId`           | `string`  | 部门标识         |
| `deptName`         | `string`  | 部门名称         |

注意：

1. 返回的数据中不包含用户密码