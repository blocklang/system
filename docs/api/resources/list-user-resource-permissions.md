# 获取登录用户对资源的权限列表

校验规则：

1. 用户已登录

注意，只要用户登录，就有权访问该 API，不需要判断用户是否有权限。

```text
GET /user/resources/{resourceId}/permissions
```

## Parameters

| Name               | Type     | Description            |
| ------------------ | -------- | ---------------------- |
| `resourceId`(path) | `string` | **Required**. 资源标识 |


注意：

1. `resourceId` 是所管理资源中某个资源的标识

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

获取成功

```text
Status: 200 OK
```

返回一个 JSON 对象：

```json
{
    "id": "resourceId",
    "canAccess": true,
    "permissions": ["query", "new"]
}
```

| Name          | Type       | Description            |
| ------------- | ---------- | ---------------------- |
| `id`          | `string`   | 资源标识               |
| `canAccess`   | `boolean`  | 是否有权访问程序模块   |
| `permissions` | `string[]` | 用户拥有的操作权限列表 |

注意：

1. `permissions` 只存储用户有权访问的操作，而不是资源的所有操作。