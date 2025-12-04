package com.example.katoapp.data.repository

import com.example.katoapp.data.model.User
import com.example.katoapp.utils.InvalidLoginException
import com.example.katoapp.utils.NetworkException
import com.example.katoapp.utils.UnknownException
import com.example.katoapp.utils.UserAlreadyExistsException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor() {
    private val authRef = FirebaseAuth.getInstance()
    private val dbRef = FirebaseFirestore.getInstance()
    val currentUser: FirebaseUser?
        get() = authRef.currentUser

    //validate email & pass
    suspend fun login(email: String, pass: String): String {
        return try {
            val result = authRef.signInWithEmailAndPassword(email, pass).await()
            result.user?.uid ?: throw UnknownException("User ID not found")
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthInvalidUserException ,
                is FirebaseAuthInvalidCredentialsException -> InvalidLoginException("Email atau password salah")
                is FirebaseNetworkException -> NetworkException("Koneksi internet bermasalah")
                else -> UnknownException(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    //create user & save to db
    suspend fun register(username: String, email: String, pass: String): String {
        return try {
            val result = authRef.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: throw UnknownException("Gagal membuat user")

            val userData = User(
                userId = user.uid,
                username = username,
                email = email
            )
            dbRef.collection("pengguna")
                .document(user.uid)
                .set(userData)
                .await()
            user.uid
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthUserCollisionException -> UserAlreadyExistsException("Email sudah terdaftar")
                is FirebaseAuthInvalidCredentialsException -> InvalidLoginException("Format email salah atau password lemah")
                is FirebaseNetworkException -> NetworkException("Koneksi internet bermasalah")
                else -> UnknownException(e.message ?: "Gagal daftar")
            }
        }
    }

    //send email reset pass
    suspend fun resetPassword(email: String) {
        try {
            authRef.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthInvalidUserException -> InvalidLoginException("Email tidak terdaftar")
                is FirebaseNetworkException -> NetworkException("Koneksi internet bermasalah")
                else -> UnknownException(e.message ?: "Gagal mengirim email reset")
            }
        }
    }

    //signOut
    fun logout() {
        authRef.signOut()
    }



}