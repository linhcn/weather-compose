package com.linhcn.weather.data.remote

import com.linhcn.weather.data.local.entities.Weather
import com.linhcn.weather.models.Result
import com.linhcn.weather.models.Error
import com.linhcn.weather.utils.getName
import retrofit2.Response
import java.util.*
import javax.inject.Inject

abstract class BaseRemoteDataSource {
    protected suspend fun <E : Any, T : ApiResponse<E>> apiCall(call: suspend () -> Response<T>): Result<E> {
        // invoke a call
        val response: Response<T>
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            return toResult()
        }

        // convert response to our custom Result
        return if (response.isSuccessful) {
            val body = response.body()
            return body?.toResult() ?: Result.Failure(Error.unknownError())
        } else {
            Result.Failure(Error.unknownError())
        }
    }

    protected suspend fun <E : Any, T : E> apiCall1(call: suspend () -> E): Result<E> {
        // invoke a call
        val response: E
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            t.printStackTrace()
            return toResult()
        }
        return Result.Success(response)
    }
}

fun <T : Any> toResult(): Result<T> {
    return Result.Failure(Error.unknownError())
}

interface RemoteDataSource {
    suspend fun getWeathersOnDate(date: Date): Result<List<Weather>>
}

class RemoteDataSourceImpl @Inject constructor(private val apiClient: ApiClient) : RemoteDataSource,
    BaseRemoteDataSource() {

    override suspend fun getWeathersOnDate(date: Date): Result<List<Weather>> {
        return apiCall1 {
            val c = Calendar.getInstance()
            c.time = date
            apiClient.getWeathersOnDate(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
}