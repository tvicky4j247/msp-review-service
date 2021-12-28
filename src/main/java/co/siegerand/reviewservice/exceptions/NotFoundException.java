package co.siegerand.reviewservice.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public NotFoundException(Throwable err) {
        super(err);
    }
}
