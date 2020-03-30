# 在角色中删除一个用户

校验规则：

1. 用户已登录
2. 用户有权访问

```text
POST /roles/{roleId}/users/{userId}/unassign
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

删除成功

```text
Status: 200 OK
```

不返回任何内容
