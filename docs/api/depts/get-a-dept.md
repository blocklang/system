# 获取一个部门信息

校验规则：

1. 用户已登录
2. 用户有权访问
3. 部门存在

```text
GET /depts/{deptId}?resid={resId}
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `deptId`(path)          | `string` | **Required**. 部门标识         |
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

部门不存在

```text
Status: 404 NOT FOUND
```

获取成功

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
