package co.siegerand.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Review {

    private int id;
    private int bookId;
    private int userId;
    private String title;
    private String content;

    public Review(ReviewEntity entity) {
        this.id = entity.getId();
        this.bookId = entity.getBookId();
        this.userId = entity.getUserId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
    }

}
