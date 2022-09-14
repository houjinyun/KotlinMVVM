package com.hjy.template.global

/**
 * 在 StateFlow 中使用时，必须改变对象才能触发更新，对复杂对象修改属性时，为了避免大量对象复制操作，用此包装一下
 */
class Resource<T>(var data: T?)