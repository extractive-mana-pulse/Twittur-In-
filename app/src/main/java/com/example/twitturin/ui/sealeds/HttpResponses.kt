package com.example.twitturin.ui.sealeds

sealed class HttpResponses {
    data class Error(val statusCode: Int) : HttpResponses() {
        val errorMessage: String
            get() = when (statusCode) {
                400 -> "Bad Request"
                401 -> "Unauthorized"
                403 -> "Forbidden"
                404 -> "Not Found"
                500 -> "Internal Server Error"
                else -> "Unknown Error"
            }
    }

//    sealed class EditUserResult {
//
//        data class Success(val editUser : EditProfile) : EditUserResult()
//        data class Error(val message: Int) : EditUserResult() {
//            private val status = HttpResponses.Error(message)
//            val errorMessageTo = status.errorMessage
//
//        }
//    }

    sealed class DeleteResult {
        data object Success : DeleteResult()
        data class Error(val message: Int) : DeleteResult() {
            val error = HttpResponses.Error(message)
            val errorMessage = error.errorMessage
        }
    }
}