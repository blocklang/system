# 新增一个资源

校验规则：

1. 用户已登录
2. 用户有权访问
3. APP 标识不能为空
4. 父资源标识不能为空
5. 资源名不能为空
6. APP 标识指定的 APP 不存在
7. 父资源下的直属资源名不能重复

```text
POST /resources
```

## Parameters

| Name                    | Type      | Description                    |
| ----------------------- | --------- | ------------------------------ |
| `Authorization`(header) | `string`  | **Required**. 登录用户的 token |
| `parentId`(body)        | `string`  | **Required**. 父资源标识       |
| `appId`(body)           | `string`  | **Required**. APP 标识         |
| `name`(body)            | `string`  | **Required**. 资源名           |
| `url`(body)             | `string`  | 访问路径                       |
| `icon`(body)            | `string`  | 资源图标                       |
| `resourceType`(body)    | `string`  | 资源类型                       |
| `description`(body)     | `string`  | 资源描述                       |
| `auth`(body)            | `string`  | 权限标识                       |

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
        "parentId": ["${filedErrorMessage}"],
        "appId": ["${filedErrorMessage}"],
        "name": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. APP 标识为空时返回 `请选择一个APP！`
2. 父资源标识为空时返回 `请选择一个父资源！`
3. 资源名为空时返回 `请输入资源名！`
4. APP 标识指定的 APP 不存在时返回 `<strong>{appId}</strong>不存在！`
5. APP 下的某资源下的资源名已被占用时返回 `<strong>{resourceName}</strong>已被占用！`

校验通过

```text
Status: 201 CREATED
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
