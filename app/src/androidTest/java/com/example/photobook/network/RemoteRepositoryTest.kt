package com.example.photobook.network

import com.example.photobook.ServiceLocator
import com.example.photobook.data.*
import com.example.photobook.utils.Constants.IMAGE_NAME
import com.google.firebase.Timestamp
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * RemoteRepositoryTest - Tests our RemoteRepository class
 */
class RemoteRepositoryTest
{
    private lateinit var remoteRepository: RemoteRepository
    private lateinit var post: Post
    private lateinit var user: User
    private lateinit var postFirestore: PostFirestore
    private lateinit var comment: Comment


    @Before
    fun init(){
        remoteRepository = RemoteRepository()
        post = Post("id", "userId", title = "title", body = "body", inserted_at = Timestamp.now())
        postFirestore = PostFirestore(id = "id", submitter_id = "userId", title = "title", body = "body", inserted_at = post.inserted_at)
        user = User("username", "userId")
        comment = Comment("commentId", "userId", Timestamp(1578762627, 828000000), "body")
    }

    @After
    fun resetService(){
        ServiceLocator.resetService()
    }
    @Test
    fun savePost_getPostById() = runBlocking {
        val result = remoteRepository.savePost(post, user)


        postFirestore.user = user

        val postSaved: PostFirestore? = remoteRepository.getPost(result.id)

        postFirestore.id = result.id
        MatcherAssert.assertThat(postSaved, `is`(postFirestore))

    }

    @Test
    fun savePostMedia_getPostById()= runBlocking {
        val media = Media("idMedia", "yes", IMAGE_NAME, "TITLE", listOf())

        val result = remoteRepository.savePostMedia(post, media, user)
        postFirestore.media = media
        postFirestore.id = result.id
        postFirestore.user = user
        val postFirestoreSaved = remoteRepository.getPost(result.id)

        MatcherAssert.assertThat(postFirestoreSaved, `is`(postFirestore))
    }
    @Test
    fun savePosts_getPosts() = runBlocking {
        val media = Media("mediaId", "yes", "image", "media", listOf("url"))

        var result = remoteRepository.savePost(post, user)
        post.id = result.id
        postFirestore.id = result.id
        postFirestore.user = user
        result = remoteRepository.savePost(post, user)
        val postFirestore1 = PostFirestore(id = result.id, submitter_id = "userId", title = "title", body = "body", user = user, inserted_at = post.inserted_at)
        result = remoteRepository.savePostMedia(post, media, user)
        val postFirestore2 = PostFirestore(id = result.id, submitter_id = "userId", title = "title", body = "body", user = user, inserted_at = post.inserted_at, media = media)



        val allPosts = remoteRepository.getPosts(limit = 2000).post

        MatcherAssert.assertThat(allPosts?.contains(postFirestore), Is.`is`(true))
        MatcherAssert.assertThat(allPosts?.contains(postFirestore1), Is.`is`(true))
        MatcherAssert.assertThat(allPosts?.contains(postFirestore2), Is.`is`(true))
    }

    @Test
    fun saveMedia_getMedia() = runBlocking {
        val media = Media("", title = "Media")

        val result = remoteRepository.saveMedia(media)
        media.id = result.id

        val loaded = remoteRepository.getMedia(result.id)
        MatcherAssert.assertThat(loaded, `is`(media))
    }

    @Test
    fun updatePost_getPostById() = runBlocking {
        val result = remoteRepository.savePost(post, user)
        postFirestore.user = user
        postFirestore.id = result.id

        remoteRepository.updatePost(postFirestore, "title", "newTitle")
        val loaded = remoteRepository.getPost(postFirestore.id)

        MatcherAssert.assertThat(loaded?.id, `is`(postFirestore.id))
        MatcherAssert.assertThat(loaded?.title, Is.`is`("newTitle") )

    }

    @Test
    fun saveComment_getCommentInPost() = runBlocking {
        val result = remoteRepository.savePost(post, user)
        comment.post_id = result.id

        val commentResult = remoteRepository.saveComment(comment)

        comment.id = commentResult?.id!!
        val loaded = remoteRepository.getPost(result.id)

        loaded?.comment_firestore?.forEach {
            it.comment.inserted_at = Timestamp(1578762627, 828000000)
        }
        MatcherAssert.assertThat(loaded?.comment_firestore, `is`(listOf(CommentFirestore(comment, mutableListOf()))))
    }


    @Test
    fun saveComments_getComments() = runBlocking {
        val comment1 = comment
        comment1.body = "new comment 1"
        val comment2 = comment
        comment2.body = "new comment 2"
        val postResult = remoteRepository.savePost(post, user)
        comment.post_id = postResult.id
        comment1.post_id = postResult.id
        comment2.post_id = postResult.id

        var result = remoteRepository.saveComment(comment)
        comment.id = result?.id!!
        result = remoteRepository.saveComment(comment1)
        comment1.id = result?.id!!
        result = remoteRepository.saveComment(comment2)
        comment2.id = result?.id!!

        val loaded = remoteRepository.getComments(postResult.id)

        loaded.comment?.forEach {
            it.inserted_at = Timestamp(1578762627, 828000000)
        }

        MatcherAssert.assertThat(loaded.comment?.contains(comment), Is.`is`(true))
        MatcherAssert.assertThat(loaded.comment?.contains(comment1), Is.`is`(true))
        MatcherAssert.assertThat(loaded.comment?.contains(comment2), Is.`is`(true))
    }

    @Test
    fun updateComment_getComment() = runBlocking {
        val resultPost = remoteRepository.savePost(post, user)
        comment.post_id = resultPost.id
        val result = remoteRepository.saveComment(comment)
        remoteRepository.saveComment(comment)
        comment.id = result?.id!!
        remoteRepository.updateComment(comment, "body", "new Body")
        val loaded = remoteRepository.getComment(comment)
        MatcherAssert.assertThat(loaded?.id, `is`(comment.id))
        MatcherAssert.assertThat(loaded?.body, Is.`is`("new Body"))
    }

}
