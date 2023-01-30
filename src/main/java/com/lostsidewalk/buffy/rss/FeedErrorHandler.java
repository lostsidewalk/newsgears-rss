package com.lostsidewalk.buffy.rss;

import com.lostsidewalk.buffy.DataAccessException;
import com.lostsidewalk.buffy.rss.audit.ErrorLogService;
import com.lostsidewalk.buffy.rss.model.error.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ControllerAdvice
public class FeedErrorHandler {

    @Autowired
    ErrorLogService errorLogService;

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(DataAccessException e) {
        errorLogService.logDataAccessException(new Date(), e);
        return internalServerErrorResponse();
    }
    //
    // utility methods
    //
    private static ResponseEntity<?> internalServerErrorResponse() {
        return new ResponseEntity<>(getErrorDetails( "Something horrible happened, please try again later.", EMPTY), INTERNAL_SERVER_ERROR);
    }

    private static ErrorDetails getErrorDetails(@SuppressWarnings("SameParameterValue") String message, @SuppressWarnings("SameParameterValue") String detailMessage) {
        return new ErrorDetails(new Date(), message, detailMessage);
    }
}
