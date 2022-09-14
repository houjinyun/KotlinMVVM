package com.hjy.template.exception

//token 过期或者失效
class TokenInvalidException(msg: String): ApiException(CODE_API_TOKEN_INVALID, msg)