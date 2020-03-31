# 获取 APP 下某个资源的直属子资源列表

逐层延迟加载。约定所有 APP 的顶层资源的父标识为 `-1`。这里获取的是 __所有__ 直属子资源，不分页。

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /resources/{resourceId}/children?appid={appId}&recursive={recursive}
```

## Parameters

| Name                    | Type     | Description                                                          |
| ----------------------- | -------- | -------------------------------------------------------------------- |
| `Authorization`(header) | `string` | **Required**. 登录用户的 token                                       |
| `resourceId`(path)      | `string` | **Required**. 资源标识                                               |
| `appId`(queryParam)     | `string` | **Required**. APP 标识                                               |
| `recursive`(queryParam) | `string` | 是否递归查询，只要包含该参数，不论为何值，就递归查询；否则逐层查询。 |

注意：

1. `resourceId` 的值为 `-1` 时表示查询 APP 下的顶层资源列表

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

返回一个 JSON 数组，其中包含所有直属子资源，资源对象字段为：

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
