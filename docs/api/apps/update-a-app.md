# 更新一个 APP 信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. APP 名称不能为空
4. 一个用户创建的 APP 名不能重复

```text
PUT /apps/{appId}?resid={resId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `resId`(queryParam)     | `string` | **Required**. 资源标识         |
| `name`(body)            | `string` | **Required**. app 名称         |
| `url`(body)             | `string` | app 访问地址                   |
| `icon`(body)            | `string` | app 图标                       |
| `description`(body)     | `string` | 详细描述                       |

## Response

用户未登录

```text
Status: 401 UNAUTHORIZED
```

用户无权访问

```text
Status: 403 FORBIDDEN
```

校验未通过

```text
Status: 422 Unprocessable Entity
```

```json
{
    "errors": {
        "name": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. APP 名称为空时返回 `请输入 APP 名称！`
2. 一个用户创建的 APP 名被占用时返回 `<strong>{appName}</strong>已被占用！`（注意当与原 APP 名相同时不提示）

更新成功

```text
Status: 200 OK
```

返回一个 JSON 对象，其中包括以下数据项

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
