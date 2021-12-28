package co.siegerand.reviewservice.model;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewRepository extends ReactiveMongoRepository<ReviewEntity, Integer> {

    Flux<ReviewEntity> findAllByBookId(int bookId);

    Mono<ReviewEntity> findByBookIdAndUserId(int bookId, int userId);

}
