package com.example.kotlinwebfluxtut.controller

import com.example.kotlinwebfluxtut.model.LightComment
import com.example.kotlinwebfluxtut.model.Response
import com.example.kotlinwebfluxtut.service.APIService
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono


@RestController
@RequestMapping(path = ["api/"], produces = [ APPLICATION_JSON_UTF8_VALUE ])
class APIController(
        private val apiService: APIService
        // APIService is something that you implemented (APIService.kt)
) {
    @RequestMapping(method = [RequestMethod.GET])
    fun getData(): Mono<ResponseEntity<List<Response>>> {
        /*
            The goal of this method is to fetch 20 posts with an even id from jsonplaceholder
            and then, for each post in parallel, fetch its comments and transforming the posts
            and comments into a nested data-structure like this:
                [
                    {
                        "postId": 1,
                        "userId": 2,
                        "title": "...",
                        "comments":
                        [
                            {
                                "email": "...",
                                "body": "..."
                            }
                        ]
                    }
                ]
         */

        return apiService.fetchPosts()
                .filter() { it -> it.userId % 2 == 0}  // Get Posts w/even userId from reactive steam
                .take(20)                           // Take only the first 20 elements from stream
                .parallel(4)
                .runOn(Schedulers.parallel())
                .map { post -> apiService.fetchComments(post.id)
                               .map { comment -> LightComment(email=comment.email, body=comment.body) }
                               .collectList()
                               .zipWith(post.toMono())
                        }
                .flatMap { it -> it }
                .map { result -> Response(
                                    postId = result.t2.id,
                                    userId = result.t2.userId,
                                    title = result.t2.title,
                                    comments = result.t1
                                 )
                     }
                .sequential()
                .collectList()
                .map { body -> ResponseEntity.ok().body(body) }
                .toMono()
    }
}