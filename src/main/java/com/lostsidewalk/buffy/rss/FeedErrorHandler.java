package com.lostsidewalk.buffy.rss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class FeedErrorHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {
        log.error("Something horrible happened while servicing a feed request due to: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Something horrible happened while servicing your request, please try again later.", INTERNAL_SERVER_ERROR);
    }
}
