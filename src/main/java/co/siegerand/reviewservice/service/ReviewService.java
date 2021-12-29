package co.siegerand.reviewservice.service;

import co.siegerand.reviewservice.model.Review;
import co.siegerand.reviewservice.model.ReviewList;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    @PostMapping(value = "/review",
            produces = "application/json",
            consumes = "application/json")
    Mono<Review> createReview(@RequestBody Review review);

    @DeleteMapping(value = "/review/{reviewId}")
    Mono<Void> deleteReview(@PathVariable int reviewId);

    @DeleteMapping(value = "/review/all/{bookId}")
    Flux<Void> deleteAllReviewsForBook(@PathVariable int bookId);

    @GetMapping(value = "/review/{id}",
            produces = "application/json")
    Mono<Review> getReview(@PathVariable(name = "id") int reviewId);

    @GetMapping(value = "/review/all/{bookId}",
            produces = "application/json")
    Mono<ReviewList> getAllReviewsForBook(@PathVariable int bookId);

    @PostMapping(value = "/review/{id}")
    Mono<Review> updateReview(@PathVariable(name = "id") int reviewId, @RequestBody Review review);

}
