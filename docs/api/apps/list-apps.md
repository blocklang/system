# 获取 APP 列表

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /apps?page={page}&resid={resId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `page`(queryParam)      | `int`    | 当前页码，从 0 开始计数        |
| `resId`(queryParam)     | `string` | **Required**. 资源标识         |

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

返回一个 JSON 对象，其中包含分页信息和 APP 列表

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

| 属性名             | 类型      | 描述             |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | app 标识         |
| `name`             | `string`  | app 名称         |
| `url`              | `string`  | app 访问地址     |
| `icon`             | `string`  | app 图标         |
| `description`      | `string`  | 详细描述         |
| `active`           | `boolean` | 是否启用         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
