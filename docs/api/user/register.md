# 注册用户

校验规则：

1. 用户名不能为空
2. 用户名不能被占用
3. 密码不能为空

```text
POST /users
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
        "username": ["${filedErrorMessage}"],
        "password": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. 用户名能为空时返回 `请输入用户名！`
2. 用户名已被占用时返回 `<strong>{username}</strong>已被占用！`
3. 密码为空时返回 `请输入密码！`

校验通过

```text
Status: 201 CREATED
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