package com.hjy.template.exception

open class ApiException(code: Int, msg: String): AppException(code, msg)

//网络连接异常的错误码
const val CODE_API_NETWORK_CONN_ERROR = 10000000

//token过期或者失效的错误码
const val CODE_API_TOKEN_INVALID = 10000001