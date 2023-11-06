package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.rss.audit.ErrorLogService;
import com.lostsidewalk.buffy.rss.model.error.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.notFound;

@Slf4j
@ControllerAdvice
public class FeedErrorHandler {

    @ExceptionHandler(DataAccessException.class)
    public static ResponseEntity<?> handleDataAccessException(DataAccessException e) {
        ErrorLogService.logDataAccessException(new Date(), e);
        return notFoundResponse();
    }

    @ExceptionHandler(Exception.class)
    public static ResponseEntity<?> handleAllExceptions(Exception ex, WebRequest request) {
        // Custom handling for all other exceptions
        return internalServerErrorResponse();
    }

    //
    // utility methods
    //
    private static ResponseEntity<?> notFoundResponse() {
        return notFound().build();
    }

    private static ResponseEntity<?> internalServerErrorResponse() {
        return new ResponseEntity<>(getErrorDetails("Something horrible happened, please try again later.", EMPTY), INTERNAL_SERVER_ERROR);
    }

    private static ErrorDetails getErrorDetails(@SuppressWarnings("SameParameterValue") String message, @SuppressWarnings("SameParameterValue") String detailMessage) {
        return new ErrorDetails(new Date(), message, detailMessage);
    }
}
