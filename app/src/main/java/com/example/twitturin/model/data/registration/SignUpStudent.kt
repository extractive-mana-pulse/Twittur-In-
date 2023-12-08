package com.example.twitturin.model.data.registration

data class SignUpStudent(
    var username: String,
    var studentId: String,
    var major: String,
    var email: String,
    var password: String,
    var kind: String
)