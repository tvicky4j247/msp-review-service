package co.siegerand.reviewservice.model;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewList {

    private final String serviceAddress;
    private final List<Review> reviews;
    private final int reviewCount;

    public ReviewList(String serviceAddress, List<Review> reviews) {
        this.serviceAddress = serviceAddress;
        this.reviews = reviews;
        this.reviewCount = reviews != null ? reviews.size() : 0;
    }

}
