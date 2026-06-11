package com.example.splitexpenses.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            syncUserToFirestore()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String, displayName: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
                
                val initials = displayName.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.first().uppercaseChar() }
                    .joinToString("")
                    .ifEmpty { "U" }

                val userData = hashMapOf(
                    "id" to user.uid,
                    "name" to displayName,
                    "email" to email,
                    "initials" to initials
                )
                db.collection("users").document(user.uid).set(userData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncUserToFirestore(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
            val document = db.collection("users").document(user.uid).get().await()
            if (!document.exists()) {
                val name = user.displayName ?: "Utilizador"
                val initials = name.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .map { it.first().uppercaseChar() }
                    .joinToString("")
                    .ifEmpty { "U" }

                val userData = hashMapOf(
                    "id" to user.uid,
                    "name" to name,
                    "email" to (user.email ?: ""),
                    "initials" to initials
                )
                db.collection("users").document(user.uid).set(userData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
