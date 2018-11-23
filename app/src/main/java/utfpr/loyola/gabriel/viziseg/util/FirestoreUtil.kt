package utfpr.loyola.gabriel.viziseg.util

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.lang.NullPointerException
import com.xwray.groupie.kotlinandroidextensions.Item
import utfpr.loyola.gabriel.viziseg.model.*
import utfpr.loyola.gabriel.viziseg.recyclerview.item.*

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")
    private val groupsCollectionRef = firestoreInstance.collection("groups")

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                        "", null, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String = "", bio: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (bio.isNotBlank()) userFieldMap["bio"] = bio
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
                }
                onListen(items)
            }
    }

    fun addGroupUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                        items.add(GroupPersonItem(it.toObject(User::class.java)!!, it.id, false, context))
                }
                onListen(items)
            }
    }

    fun addGroupsListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("groups")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Groups listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    val group = it.toObject(Group::class.java)!!
                    if (group.userIds.any { x -> x == FirebaseAuth.getInstance().currentUser?.uid }) {
                        items.add(GroupItem(group, it.id, context))
                    }
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun createGroup(groupName: String = "",
                    groupDescription: String = "",
                    userIds: MutableList<String>) {

        val newGroup = groupsCollectionRef.document()
        newGroup.set(Group(groupName, groupDescription, userIds))
    }

    fun getOrCreateChatChannel(otherUserId: String?,
                               groupId: String?,
                               onComplete: (channelId: String) -> Unit) {
        if (!groupId.isNullOrBlank()) {
            currentUserDocRef.collection("engagedChatChannels")
                .document(groupId!!).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    val newChannel = chatChannelsCollectionRef.document()
                    firestoreInstance.collection("groups")
                        .document(groupId).get().addOnSuccessListener { groupItem ->
                            val group = groupItem.toObject(Group::class.java!!)
                            if (group != null) {
                                newChannel.set(ChatChannel(group.userIds))
                                group.userIds.forEach {
                                    val user = firestoreInstance.document("users/$it")
                                    user.collection("engagedChatChannels")
                                        .document(groupId)
                                        .set(mapOf("channelId" to newChannel.id))
                                }
                            }
                        }

                    currentUserDocRef
                        .collection("engagedChatChannels")
                        .document(groupId)
                        .set(mapOf("channelId" to newChannel.id))

                    firestoreInstance.collection("groups")
                        .document(groupId)
                        .update(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
        } else {
            currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId!!).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }

                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val newChannel = chatChannelsCollectionRef.document()
                    newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                    currentUserDocRef
                        .collection("engagedChatChannels")
                        .document(otherUserId)
                        .set(mapOf("channelId" to newChannel.id))

                    firestoreInstance.collection("users").document(otherUserId)
                        .collection("engagedChatChannels")
                        .document(currentUserId)
                        .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
        }

    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessagesListener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT)
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }

    //region FCM
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }
    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
    //endregion FCM
}