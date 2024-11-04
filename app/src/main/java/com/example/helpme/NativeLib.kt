package com.example.helpme

class NativeLib {
    init {
        System.loadLibrary("native-lib")
    }

    external fun addUser(username: String, password: String): Boolean
    external fun isUserValid(username: String, password: String): Boolean
    external fun isUserExists(username: String): Boolean
    external fun freeUsers()
}