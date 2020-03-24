# 获取 APP 的角色列表

获取 __指定 APP__ 下的角色列表。

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /roles?page={page}&appid={appId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `page`(queryParam)      | `int`    | 当前页码，从 0 开始计数        |
| `appId`(queryParam)     | `string` | **Required**. APP 标识         |

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

返回一个 JSON 对象，其中包含分页信息和角色列表

```json
{
    "totalPages": 1,
    "number": 0,
    "size": 10,
    "first": true,
    "last": true,
    "empty": true,
    "content": []
}
```

默认每页显式 15 条记录。

`content` 数组中的 JSON 对象字段为：

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 角色标识         |
| `appId`            | `string`  | APP 标识         |
| `name`             | `string`  | 角色名称         |
| `description`      | `string`  | 角色描述         |
| `seq`              | `int`     | 显示顺序         |
| `active`           | `boolean` | 是否启用         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
