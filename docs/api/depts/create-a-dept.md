# 新增一个部门

校验规则：

1. 父部门标识不能为空
2. 父部门标识指定的部门不存在
3. 部门名称不能为空
4. 父部门下不存在同名部门

```text
POST /depts?resid={resId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `resId`(queryParam)     | `string` | **Required**. 资源标识         |
| `parentId`(body)        | `string` | **Required**. 父部门标识       |
| `name`(body)            | `string` | **Required**. 部门名称         |

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
        "name": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. 父部门标识为空时返回 `请选择一个父部门！`
2. 部门名称为空时返回 `请输入部门名称！`
3. 父部门标识指定的部门不存在时返回 `<strong>{parentId}</strong>不存在！`
4. 父部门下的部门名已被占用时返回 `<strong>{deptName}</strong>已被占用！`

校验通过

```text
Status: 201 CREATED
```

返回一个 JSON 对象，其中包括部门信息

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 部门标识         |
| `parentId`         | `string`  | 父部门标识       |
| `name`             | `string`  | 部门名称         |
| `seq`              | `int`     | 显示顺序         |
| `active`           | `boolean` | 是否启用         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
