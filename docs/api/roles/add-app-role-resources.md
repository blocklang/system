# 批量保存 APP 下一个角色的所有授权的资源

先删除角色下的所有资源，然后在插入。

校验规则：

1. 用户已登录
2. 用户有权访问

```text
PUT /roles/{roleId}/resources
```

## Parameters

| Name                    | Type       | Description                    |
| ----------------------- | ---------- | ------------------------------ |
| `Authorization`(header) | `string`   | **Required**. 登录用户的 token |
| `roleId`(path)          | `string`   | **Required**. 角色标识         |
| `appId`(body)           | `string`   | **Required**. APP 标识         |
| `resources`(body)       | `string[]` | 资源标识列表                   |

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

用户无权访问

```text
Status: 403 FORBIDDEN
```

如果更新成功

```text
Status: 200 OK
```
返回一个空的 JSON 对象
