package co.siegerand.reviewservice.service;

import co.siegerand.reviewservice.exceptions.InvalidInputException;
import co.siegerand.reviewservice.exceptions.NotFoundException;
import co.siegerand.reviewservice.model.Review;
import co.siegerand.reviewservice.model.ReviewEntity;
import co.siegerand.reviewservice.model.ReviewList;
import co.siegerand.reviewservice.model.ReviewRepository;
import co.siegerand.reviewservice.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;
import java.util.stream.Collectors;

@RestController
public class ReviewController implements ReviewService {

    private final ReviewRepository repository;
    private final String serviceAddress;
    private final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewRepository repository, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.serviceAddress = serviceUtil.getServiceAddress();
    }

    @Override
    public Mono<Review> createReview(Review review) {
        // check valid id
        if (review.getId() < 1) throw new InvalidInputException("Review id cannot be less than 1");

        // check review doesn't already exist
        return repository.save(new ReviewEntity(review))
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(InvalidInputException::new)
                .map(Review::new);
    }

    @Override
    public Mono<Void> deleteReview(int reviewId) {
        return repository.findById(reviewId)
                .log(LOG.getName(), Level.FINE)
                .map(repository::delete)
                .flatMap(e -> e);
    }

    @Override
    public Flux<Void> deleteAllReviewsForBook(int bookId) {
        if (bookId < 1) {
            throw new InvalidInputException("Invalid book id provided, " + bookId);
        }
        return repository.findAllByBookId(bookId).flatMap(repository::delete);
    }

    @Override
    public Mono<Review> getReview(int reviewId) {
        if (reviewId < 1) throw new InvalidInputException("Invalid review id provided " + reviewId);

        return repository.findById(reviewId)
                .switchIfEmpty(Mono.error(new NotFoundException("No review found for id " + reviewId)))
                .log(LOG.getName(), Level.FINE)
                .map(Review::new);
    }

    @Override
    public Mono<ReviewList> getAllReviewsForBook(int bookId) {
        if (bookId < 1) throw new InvalidInputException("Invalid book id provided " + bookId);

        return repository.findAllByBookId(bookId)
                .log(LOG.getName(), Level.FINE)
                .switchIfEmpty(Mono.error(new NotFoundException("No reviews found for book id: " + bookId)))
                .collectList()
                .map(e -> new ReviewList(serviceAddress, e.stream().map(Review::new).collect(Collectors.toList())));
    }

    @Override
    public Mono<Review> updateReview(int reviewId, Review review) {

        return repository.findById(reviewId)
                .map(entity -> setEntityDetails(review, entity))
                .flatMap(repository::save)
                .map(Review::new);
    }


    // UTIL METHODS
    private ReviewEntity setEntityDetails(Review review, ReviewEntity entityToModify) {
        entityToModify.setContent(review.getContent());
        entityToModify.setTitle(review.getTitle());
        return entityToModify;
    }
}
