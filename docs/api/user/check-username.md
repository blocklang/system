# 校验用户名

校验规则：

1. 用户名不能为空
2. 用户名未被占用

```text
POST /users/check-username
```

## Parameters

Form data

| Name             | Type     | Description          |
| ---------------- | -------- | -------------------- |
| `username`(body) | `string` | **Required**. 用户名 |

## Response

校验未通过

```text
Status: 422 Unprocessable Entity
```

```json
{
    "errors": {
        "username": ["${filedErrorMessage}"]
    }
}
```

`filedErrorMessage` 的值为：

1. 用户名为空时返回 `请输入用户名！`
2. 用户名已被占用时返回 `<strong>{username}</strong>已被占用！`

校验通过

```text
Status: 200 OK
```

返回空 json 对象。