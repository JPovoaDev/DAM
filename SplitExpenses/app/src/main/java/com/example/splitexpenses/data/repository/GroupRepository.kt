package com.example.splitexpenses.data.repository

import com.example.splitexpenses.data.Expense
import com.example.splitexpenses.data.Group
import com.example.splitexpenses.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class GroupRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun observeGroups(): Flow<List<Group>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("groups")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val groupsList = snapshot.documents.mapNotNull { it.toObject(Group::class.java) }
                    .filter { group ->
                        group.members.any { it.id == currentUserId }
                    }
                trySend(groupsList)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createGroup(group: Group): Result<Unit> {
        return try {
            db.collection("groups").document(group.id).set(group).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeGroup(groupId: String): Flow<Group?> = callbackFlow {
        val listener = db.collection("groups").document(groupId)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val group = snapshot.toObject(Group::class.java)
                trySend(group)
            }
        awaitClose { listener.remove() }
    }

    fun observeExpenses(groupId: String): Flow<List<Expense>> = callbackFlow {
        val listener = db.collection("groups").document(groupId).collection("expenses")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val expensesList = snapshot.documents.mapNotNull { it.toObject(Expense::class.java) }
                trySend(expensesList)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateGroupBalances(groupId: String, balances: Map<String, Double>): Result<Unit> {
        return try {
            db.collection("groups").document(groupId)
                .update("userBalances", balances).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun payMyPartOfExpense(groupId: String, expenseId: String, currentUserId: String): Result<Unit> {
        return try {
            val expenseRef = db.collection("groups").document(groupId).collection("expenses").document(expenseId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(expenseRef)
                val expense = snapshot.toObject(Expense::class.java)
                if (expense != null && !expense.settledParticipants.contains(currentUserId)) {
                    val newSettled = expense.settledParticipants + currentUserId
                    transaction.update(expenseRef, "settledParticipants", newSettled)
                }
                null
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findUserByEmail(email: String): Result<User?> {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get().await()
            
            if (querySnapshot.isEmpty) {
                Result.success(null)
            } else {
                val user = querySnapshot.documents.first().toObject(User::class.java)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMemberToGroup(groupId: String, members: List<User>): Result<Unit> {
        return try {
            db.collection("groups").document(groupId)
                .update("members", members).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addExpense(groupId: String, expense: Expense): Result<Unit> {
        return try {
            db.collection("groups").document(groupId).collection("expenses")
                .document(expense.id)
                .set(expense).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
