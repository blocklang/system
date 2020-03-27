# 获取一位角色信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. 角色存在

```text
GET /roles/{roleId}
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

角色不存在

```text
Status: 404 NOT FOUND
```

获取成功

```text
Status: 200 OK
```

返回一个 JSON 对象，其中包括角色信息

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 角色标识         |
| `appId`            | `string`  | APP 标识         |
| `appName`          | `string`  | APP 名称         |
| `name`             | `string`  | 角色名称         |
| `description`      | `string`  | 角色描述         |
| `seq`              | `int`     | 显示顺序         |
| `active`           | `boolean` | 是否启用         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
