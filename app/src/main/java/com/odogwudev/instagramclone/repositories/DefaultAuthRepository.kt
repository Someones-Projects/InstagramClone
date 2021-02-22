package com.odogwudev.instagramclone.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.odogwudev.instagramclone.data.entities.User
import com.odogwudev.instagramclone.others.Resource
import com.odogwudev.instagramclone.others.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository {
    class DefaultAuthRepository : AuthRepository {

        val auth = FirebaseAuth.getInstance()
        val users = FirebaseFirestore.getInstance().collection("users")
        override suspend fun register(
            email: String,
            username: String,
            password: String
        ): Resource<AuthResult> {
            return withContext(Dispatchers.IO) {
                safeCall {
                    val result = auth.createUserWithEmailAndPassword(email, password).await()
                    val uid = result.user?.uid!!
                    val user = User(uid, username)
                    users.document(uid).set(user).await()
                    Resource.Success(result)
                }
            }
        }

        override suspend fun login(email: String, password: String): Resource<AuthResult> {
            return withContext(Dispatchers.IO) {
                safeCall {
                    val result = auth.signInWithEmailAndPassword(email, password).await()
                    Resource.Success(result)
                }
            }
        }
    }
}