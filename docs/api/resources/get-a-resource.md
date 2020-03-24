# 获取一个资源

校验规则：

1. 用户已登录
2. 用户有权访问
3. 资源存在

```text
GET /resources/{resourceId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `resourceId`(path)      | `string` | **Required**. 资源标识         |

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

用户无权访问

```text
Status: 403 FORBIDDEN
```

资源不存在

```text
Status: 404 NOT FOUND
```

获取成功

```text
Status: 200 OK
```

返回一个 JSON 对象，其中的字段为：

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 资源标识         |
| `appId`            | `string`  | APP 标识         |
| `name`             | `string`  | 资源名称         |
| `parentId`         | `string`  | 父资源标识       |
| `seq`              | `int`     | 显示顺序         |
| `url`              | `string`  | 访问路径         |
| `icon`             | `string`  | 资源图标         |
| `resourceType`     | `string`  | 资源类型         |
| `description`      | `string`  | 资源描述         |
| `active`           | `boolean` | 是否启用         |
| `auth`             | `string`  | 权限标识         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
