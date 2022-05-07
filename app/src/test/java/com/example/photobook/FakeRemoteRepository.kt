package com.example.photobook

import android.app.Activity
import com.example.photobook.data.*
import com.example.photobook.network.IRemoteRepository
import com.example.photobook.utils.VoteType
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.mockito.Mock
import java.util.concurrent.Executor

/**
 * FakeRemoteRepository - A fake remote repository for testing purpose
 * it will helps to get some fake data for our unit and integration test
 */
class FakeRemoteRepository: IRemoteRepository
{
    private val comment = Comment("idComment", "userId", Timestamp(35, 45), post_id = "id1", body = "body", level = 2)
    @Mock
    private lateinit var successTask: Task<Void>
    @Mock
    private lateinit var failureTask: Task<Void>

    init {
        successTask = object : Task<Void>() {

            override fun addOnCompleteListener(p0: OnCompleteListener<Void>): Task<Void> {
                p0.onComplete(successTask)
                return super.addOnCompleteListener(p0)
            }
            override fun addOnFailureListener(p0: OnFailureListener): Task<Void> {
                p0.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<Void> {
                p1.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<Void> {
                p1.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in Void>): Task<Void> {
                p0.onSuccess(successTask.result)
                return successTask
            }


            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(successTask.result)
                return successTask
            }

            override fun getException(): Exception? {
                return null
            }

            override fun getResult(): Void? {
                return null
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): Void? {
                return null
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(successTask.result)
                return successTask
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return true
            }

            override fun addOnCompleteListener(
                p0: Activity,
                p1: OnCompleteListener<Void>
            ): Task<Void> {
                super.addOnCompleteListener(p0, p1)
                p1.onComplete(successTask)
                return successTask
            }

            override fun addOnCompleteListener(
                p0: Executor,
                p1: OnCompleteListener<Void>
            ): Task<Void> {
                super.addOnCompleteListener(p0, p1)
                p1.onComplete(successTask)
                return successTask
            }

        }

        failureTask = object : Task<Void>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<Void> {
                p0.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<Void> {
                p1.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<Void> {
                p1.onFailure(java.lang.Exception())
                return failureTask
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in Void>): Task<Void> {
                p0.onSuccess(failureTask.result)
                return successTask
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(failureTask.result)
                return successTask
            }

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in Void>
            ): Task<Void> {
                p1.onSuccess(failureTask.result)
                return successTask
            }

            override fun getException(): java.lang.Exception? {
                return null
            }

            override fun getResult(): Void? {
                return null
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): Void? {
                return null
            }

            override fun isCanceled(): Boolean {
                return false
            }

            override fun isComplete(): Boolean {
                return true
            }

            override fun isSuccessful(): Boolean {
                return false
            }

            override fun addOnCompleteListener(p0: OnCompleteListener<Void>): Task<Void> {
                p0.onComplete(failureTask)
                super.addOnCompleteListener(p0)
                return failureTask
            }

            override fun addOnCompleteListener(
                p0: Activity,
                p1: OnCompleteListener<Void>
            ): Task<Void> {
                super.addOnCompleteListener(p0, p1)
                p1.onComplete(failureTask)
                return failureTask
            }

            override fun addOnCompleteListener(
                p0: Executor,
                p1: OnCompleteListener<Void>
            ): Task<Void> {
                super.addOnCompleteListener(p0, p1)
                p1.onComplete(failureTask)
                return failureTask
            }
        }
    }

    init {

    }

    override suspend fun saveMedia(media: Media): Result {
        return Result(media.id, successTask)

    }

    override suspend fun savePostMedia(post: Post, media: Media, user: User): Result {
        return Result(post.id, successTask)
    }


    override suspend fun savePost(post: Post, user: User): Result {
        return Result(post.id, successTask)
    }


    override suspend fun getPosts(limit:Long): PostResponse {
        return PostResponse(post = listOf(PostFirestore("title", "body", id = "id1")))
    }

    override suspend fun updatePost(post: PostFirestore, field: String, value: Any): Task<Void>
    {
        TODO("Not yet implemented")
    }

    override suspend fun getPostSnap(id: String): QuerySnapshot {
        TODO("Not yet implemented")
    }

    override suspend fun getPost(
        id: String
    ): PostFirestore {
        return PostFirestore("title", "body", id = "id1")
    }


    override suspend fun saveVote(
        frameworkData: PostFirestore,
        voteType: VoteType
    ): Result {
        @Suppress("UNCHECKED_CAST")
        val result = successTask
        return Result(frameworkData.id, result)
    }

    override suspend fun getPostVote(userId: String, id: String): PostVote? {
        TODO("Not yet implemented")
    }

    override suspend fun getPostVoteSnap(userId: String, id: String): QuerySnapshot? {
        TODO("Not yet implemented")
    }


    override suspend fun saveComment(comment: Comment): Result {
        return Result(comment.id, successTask)
    }


    override suspend fun getComments(postId: String): CommentResponse {
        return CommentResponse(listOf(comment))
    }

    override suspend fun getComment(comment: Comment): Comment {
        return comment
    }

    override suspend fun getMedia(id: String): Media? {
        TODO("Not yet implemented")
    }

    override suspend fun updateComment(comment: Comment, field: String, value: Any): Task<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPosts() {
        TODO("Not yet implemented")
    }

    override suspend fun getCommentSnap(id: String): Task<QuerySnapshot> {
        TODO("Not yet implemented")
    }

}