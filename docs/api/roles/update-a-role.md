# 更新一位角色信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. APP 标识不能为空
4. 角色名不能为空
5. APP 标识指定的 APP 不存在
6. APP 下的角色名不存在

```text
PUT /roles/{roleId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `roleId`(path)          | `string` | **Required**. 角色标识         |
| `appId`(body)           | `string` | **Required**. APP 标识         |
| `name`(body)            | `string` | **Required**. 角色名称         |
| `description`(body)     | `string` | 角色描述                       |

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
        "name": ["${filedErrorMessage}"],
        "appId": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. APP 标识为空时返回 `请选择一个APP！`
2. 角色名为空时返回 `请输入角色名！`
3. APP 标识指定的 APP 不存在时返回 `<strong>{appId}</strong>不存在！`
4. APP 下的角色名已被占用时返回 `<strong>{roleName}</strong>已被占用！`（注意当与原角色名相同时不提示）

更新成功

```text
Status: 200 OK
```

返回一个 JSON 对象，其中包括角色信息

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
