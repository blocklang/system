# 获取登录用户能访问的资源列表

从 `resourceId` 的值为 `-1` 起逐层加载。

校验规则：

1. 用户已登录
2. 管理员有权访问所有资源，包括失效的功能模块、程序模块
3. 普通用户只能访问授权模块，并要排除掉失效的功能模块、程序模块和为失效角色配置资源

注意，只要用户登录，就有权访问该 API，不需要判断用户是否有权限。

```text
GET /user/resources/{resourceId}/children
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

返回一个 JSON 数组，其中的对象包含以下字段：

| Name       | Type     | Description |
| ---------- | -------- | ----------- |
| `id`       | `string` | 资源标识    |
| `appId`    | `string` | APP 标识    |
| `name`     | `string` | 资源名称    |
| `parentId` | `string` | 父资源标识  |
| `url`      | `string` | 访问路径    |
| `icon`     | `string` | 资源图标    |
| `type`     | `string` | 资源类型    |
