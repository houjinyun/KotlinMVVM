package com.hjy.template.exception

//网络连接异常
class NetworkConnException(msg: String): ApiException(CODE_API_NETWORK_CONN_ERROR, msg)