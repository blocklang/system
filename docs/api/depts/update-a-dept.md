# 更新一位部门信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. 父部门标识不能为空
4. 父部门标识指定的部门不存在
5. 部门名称不能为空
6. 父部门下不存在同名部门

```text
PUT /depts/{deptId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `deptId`(path)          | `string` | **Required**. 部门标识         |
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
4. 父部门下的部门名已被占用时返回 `<strong>{deptName}</strong>已被占用！`（注意当与原角色名相同时不提示）

更新成功

```text
Status: 200 OK
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
