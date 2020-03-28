# 获取指定部门下的直属子部门列表

逐层延迟加载。最顶层部门的父标识为 `-1`。返回所有直属子部门，不需要分页。

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /depts/{deptId}/children
```

## Parameters

| Name                    | Type     | Description                    |
| ----------------------- | -------- | ------------------------------ |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token |
| `deptId`(path)          | `string` | **Required**. 部门标识         |

查出 `deptId` 部门的所有直属子部门。

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

返回一个 JSON 数组，其中包含所有直属子部门，部门对象字段为：

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 部门标识         |
| `parentId`         | `string`  | 父部门标识       |
| `name`             | `string`  | 部门名称         |
| `seq`              | `int`     | 显示顺序         |
| `active`           | `boolean` | 是否启用         |
| `hasChildren`      | `boolean` | 是否含有子部门   |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |
