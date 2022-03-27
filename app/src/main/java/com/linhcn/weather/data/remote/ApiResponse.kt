package com.linhcn.weather.data.remote

import com.linhcn.weather.models.Error
import com.linhcn.weather.models.Result

class ApiResponse<out E : Any>(val code: Int, val message: String = "", val entity: E?)
fun <E : Any> ApiResponse<E>.toResult(): Result<E> {
    return if (this.code == 0) {
        Result.Failure(Error(this.code, this.message))
    } else {
        Result.Success(this.entity)
    }
}