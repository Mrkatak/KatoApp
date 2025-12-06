package com.example.katoapp.data.repository

import com.example.katoapp.data.model.User
import com.example.katoapp.utils.InvalidLoginException
import com.example.katoapp.utils.NetworkException
import com.example.katoapp.utils.UnknownException
import com.example.katoapp.utils.UserAlreadyExistsException
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val googleSignInClient: GoogleSignInClient
) {
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

    //helper utk get intent signIn
    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    //create user login google & save to db
    suspend fun loginWithGoogle(idToken: String): String {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken , null)
            val result = authRef.signInWithCredential(credential).await()
            val user = result.user ?: throw UnknownException("User Google tidak ditemukan")

            //cek user data
            val docRef = dbRef.collection("pengguna").document(user.uid)
            val docSnapshot = docRef.get().await()

            //jika belum terdaftar
            if (!docSnapshot.exists()) {
                val userData = User(
                    userId = user.uid ,
                    username = user.displayName ?: "User Google" ,
                    email = user.email ?: ""
                )
                docRef.set(userData).await()
            }

            user.uid
        } catch (e: Exception) {
            throw e
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

    //get user data
    suspend fun getUserData(uid: String): User? {
        return try {
            val snapshot = dbRef.collection("pengguna")
                .document(uid)
                .get()
                .await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



}