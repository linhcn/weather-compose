package com.linhcn.weather.models

data class Error(val code: Int, val message: String = "") {

    companion object {
        const val EMPTY_CODE = -2
        fun unknownError() = Error(-1, "Unknown error!")
        fun emptyData() = Error(EMPTY_CODE, "Empty data")
    }
}

fun Error.isEmptyData(): Boolean {
    return (code == Error.EMPTY_CODE)
}