package com.example.katoapp.utils

//translate error
class UserAlreadyExistsException(message: String) : Exception(message)
class InvalidLoginException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
class UnknownException(message: String) : Exception(message)