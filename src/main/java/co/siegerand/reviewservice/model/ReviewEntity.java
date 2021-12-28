package co.siegerand.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(name = "review-book-id", unique = true, def = "{'id': 1, 'bookId' : 1}")
public class ReviewEntity {

    @Id
    private int id;

    @Version
    private int version;

    private int bookId;
    private int userId;
    private String title;
    private String content;

    public ReviewEntity(int id, int bookId, int userId, String title, String content) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public ReviewEntity(Review review) {
        this.id = review.getId();
        this.bookId = review.getBookId();
        this.userId = review.getUserId();
        this.title = review.getTitle();
        this.content = review.getContent();
    }

}
