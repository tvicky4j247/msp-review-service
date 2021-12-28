package co.siegerand.reviewservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class HttpErrorInfo {

    private final HttpStatus httpStatus;
    private final String path;
    private final String message;

}
