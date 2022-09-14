package com.hjy.template.exception

//所有自定义异常
open class AppException(val code: Int, msg: String): Exception(msg)