# 用户登录

校验规则：

1. 用户名或密码无效

```text
POST /user/login
```

## Parameters

| Name             | Type     | Description          |
| ---------------- | -------- | -------------------- |
| `username`(body) | `string` | **Required**. 用户名 |
| `password`(body) | `string` | **Required**. 密码   |

## Response

校验未通过

```text
Status: 422 Unprocessable Entity
```

```json
{
    "errors": {
        "globalErrors": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. 用户名能为空时返回 `用户名或密码无效！`

校验通过

```text
Status: 200 OK
```

返回一个 json 对象

```json
{
    "user": {}
}
```

user 对象包括以下数据项

| 属性名     | 类型     | 描述      |
| ---------- | -------- | --------- |
| `username` | `string` | 用户名    |
| `token`    | `string` | jwt token |