package com.hjy.template.exception

//业务异常
class BusinessException(code: Int, msg: String): ApiException(code, msg)