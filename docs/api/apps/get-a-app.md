# 获取一个 APP 信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. APP 信息存在

```text
GET /apps/{appId}?resid={resId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `appId`(path)           | `string` | **Required**. app 标识         |
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

APP 信息不存在

```text
Status: 404 NOT FOUND
```

获取成功

```text
Status: 200 OK
```

返回一个 json 对象，包括以下数据项

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