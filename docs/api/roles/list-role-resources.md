# 获取为一个角色分配的资源列表

一个 APP 下的角色只能分配该 APP 下的资源。

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /roles/{roleId}/resources
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

返回一个 JSON 数组，其中包含资源标识列表

```json
["{resourceId}"]
```