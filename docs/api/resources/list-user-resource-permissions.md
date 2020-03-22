# 获取登录用户对资源的权限列表

校验规则：

1. 用户已登录
2. 管理员有权访问所有资源，包括失效的功能模块、程序模块和操作按钮

注意，只要用户登录，就有权访问该 API，不需要判断用户是否有权限。

```text
GET /user/resources/{resourceId}/permissions
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `resourceId`(path)      | `string` | **Required**. 资源标识         |


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

1. `canAccess` 属于程序模块一级的权限，用于判断登录用户是否有权访问程序模块
2. `permissions` 属于操作一级的权限，只存储用户有权执行的操作，而不是资源的所有操作
3. `canAccess` 和 `permissions` 必须显式独立设置，互不依赖，遵循“皮之不存，毛将焉附”的原则，即使 `permissions` 中有操作权限，`canAccess` 为 `false` 时也无权访问程序模块
