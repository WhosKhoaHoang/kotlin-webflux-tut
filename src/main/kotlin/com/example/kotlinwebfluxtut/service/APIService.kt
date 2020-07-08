package com.example.kotlinwebfluxtut.service

import com.example.kotlinwebfluxtut.model.Comment
import com.example.kotlinwebfluxtut.model.Post
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux


@Service
class APIService {

    // Note how bodyToFlux() takes the body of a JSON response given by
    // jsonplaceholder and converts it to a reactive data stream.

    fun fetchComments(postId: Int): Flux<Comment> = fetch("posts/$postId/comments")
                                                    .bodyToFlux(Comment::class.java)

    fun fetchPosts(): Flux<Post> = fetch("/posts")
                                   .bodyToFlux(Post::class.java)

    fun fetch(path: String): WebClient.ResponseSpec {
        val client = WebClient.create("http://jsonplaceholder.typicode.com/")
        return client.get().uri(path).retrieve()
    }
}