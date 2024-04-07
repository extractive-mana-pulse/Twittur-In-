package com.example.twitturin.auth.domain.model

data class SignUpStudent(

    var fullName : String,
    var username : String,
    var studentId : String,
    var major : String,
    var password : String,
    var kind : String

)