# 获取一位用户信息

校验规则：

1. 用户已登录
2. 用户有权访问

```text
GET /users/{userId}?resid={resId}
```

## Parameters

| Name                | Type     | Description            |
| ------------------- | -------- | ---------------------- |
| `userId`(path)      | `string` | **Required**. 用户标识 |
| `resId`(queryParam) | `string` | **Required**. 资源标识 |

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

返回一个 JSON 对象，其中包括用户信息

| Name               | Type      | Description      |
| ------------------ | --------- | ---------------- |
| `id`               | `string`  | 用户标识         |
| `username`         | `string`  | 用户名           |
| `nickname`         | `string`  | 昵称             |
| `sex`              | `string`  | 性别             |
| `phoneNumber`      | `string`  | 手机号码         |
| `admin`            | `boolean` | 是否管理员       |
| `lastSignInTime`   | `string`  | 最近登录时间     |
| `signInCount`      | `int`     | 登录次数         |
| `createUserId`     | `string`  | 创建用户标识     |
| `createTime`       | `string`  | 创建时间         |
| `lastUpdateUserId` | `string`  | 最近修改用户标识 |
| `lastUpdateTime`   | `string`  | 最近修改时间     |

注意：

1. 返回的数据中不包含用户密码