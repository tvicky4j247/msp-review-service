package co.siegerand.reviewservice;

import co.siegerand.reviewservice.model.Review;
import co.siegerand.reviewservice.model.ReviewEntity;
import co.siegerand.reviewservice.model.ReviewList;
import co.siegerand.reviewservice.model.ReviewRepository;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
                properties = {"eureka.client.enabled=false"})
class ReviewServiceApplicationTests extends MongoDBInstance {

    private final ReviewRepository repository;
    private final WebTestClient webTestClient;

    @Autowired
    public ReviewServiceApplicationTests(ReviewRepository repository, WebTestClient webTestClient) {
        this.repository = repository;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void deleteData() {
        repository.deleteAll().block();
    }

    @Test
    void createSingleReview() {
        int bookId = 1;
        int reviewId = 1;

        Review review = new Review(reviewId, bookId, 1, "My Book", "My Test Book Content");
        
        // make request and confirm response
        webTestClient.post()
                .uri("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(review), Review.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

        // confirm review added
        Review returnedReview = webTestClient.get()
            .uri("/review/" + reviewId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Review.class).returnResult().getResponseBody();

        Assertions.assertEquals(review.getId(), returnedReview.getId());
    }

    @Test
    void getReview() {
        int bookId = 1;
        int reviewId = 1;

        // create on db
        Review createdReview = createReviewOnDb(reviewId, bookId);

        // check for value in db
        Review review = webTestClient.get()
                .uri("/review".concat("/" + reviewId))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(Review.class)
                .returnResult().getResponseBody();

        Assertions.assertNotNull(review);
        Assertions.assertEquals(review, createdReview);

    }

    @Test
    void addAndGetMultipleReviews() {
        int bookId = 1;
        int reviewId1 = 1;
        int reviewId2 = 2;

        // create two reviews on db
        Review review1 = createReviewOnDb(reviewId1, bookId);
        createReviewOnDb(reviewId2, bookId);

        // retrieve all reviews for bookId 1 and confirm there are two
        ReviewList reviewList = webTestClient.get()
            .uri("/review/all/" + bookId)
            .exchange()
            .expectBody(ReviewList.class)
            .returnResult().getResponseBody();

        Assertions.assertNotNull(reviewList);
        Assertions.assertEquals(2, reviewList.getReviewCount());
        Collections.sort(reviewList.getReviews(), ((a, b) -> a.getId() < b.getId() ? 0 : 1));
        Assertions.assertEquals(review1, reviewList.getReviews().get(0));
    }

    @Test
    void getReviewFail() {
        // attempt to get non-existent review from service. Expect 404
        webTestClient.get()
            .uri("/review/" + 1)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteReview() {

        int reviewId = 1;
        int bookId = 1;

        // create review
        createReviewOnDb(reviewId, bookId);

        // delete review
        webTestClient.delete()
            .uri("/review/" + reviewId)
            .exchange()
            .expectStatus().isOk();

        // confirm delete
        webTestClient.get()
            .uri("/review/" + reviewId)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteAllReviewsForBook() {
        int bookId = 1;
        
        // create multiple book reviews
        createReviewOnDb(1, bookId);
        createReviewOnDb(2, bookId);

        // delete reviews
        webTestClient.delete()
            .uri("/review/all/" + bookId)
            .exchange()
            .expectStatus().isOk();

        // confirm deletion
        webTestClient.get()
            .uri("/review/all/" + bookId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateReview() {
        int bookId = 1;
        int reviewId = 2;

        createReviewOnDb(reviewId, bookId);

        // create new review
        String newBookContent = "New Book Content";
        Review newReview = new Review(reviewId, bookId, 1, "Book Title", newBookContent);

        // call update api
        webTestClient.post()
            .uri("/review/" + reviewId)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newReview)
            .exchange()
            .expectStatus().isOk();

        // verify update
        Review returnedReview = webTestClient.get()
            .uri("/review/" + reviewId)
            .exchange()
            .expectBody(Review.class).returnResult().getResponseBody();

        Assertions.assertEquals(newReview, returnedReview);
    }

    Review createReviewOnDb(int reviewId, int bookId) {
        return new Review(repository.save(new ReviewEntity(reviewId, bookId, 1, "Book Title", "Book Content"))
                .block());
    }
}
