package ru.practicum.shareit.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.global.exception.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String PATH = "path";

    @ExceptionHandler({NotFoundException.class, NotItemOwnerException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Map<String, Object> handleNotFound(RuntimeException ex, WebRequest request) {
        log.error("Error: {}", ex.getMessage(), ex);
        Map<String, Object> responseBody = getGeneralErrorBody(HttpStatus.NOT_FOUND, request);
        responseBody.put(ERROR, ex.getMessage());
        return responseBody;
    }

    @ExceptionHandler({NotItemAvailableException.class, BadBookingDateException.class, BadApproveStatusException.class,
            BadStateException.class, NotItemBookedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected Map<String, Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.error("Error: {}", ex.getMessage(), ex);
        Map<String, Object> responseBody = getGeneralErrorBody(HttpStatus.BAD_REQUEST, request);
        responseBody.put(ERROR, ex.getMessage());
        return responseBody;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected Map<String, Object> handleAllException(final Exception ex, WebRequest request) {
        log.error("Error: {}", ex.getMessage(), ex);
        Map<String, Object> responseBody = getGeneralErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, request);
        responseBody.put(ERROR, ex.getMessage());
        return responseBody;
    }

    private Map<String, Object> getGeneralErrorBody(HttpStatus status,
                                                    WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, OffsetDateTime.now());
        body.put(STATUS, status.value());
        body.put(PATH, getRequestURI(request));
        return body;
    }

    private String getRequestURI(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            HttpServletRequest requestHttp = ((ServletWebRequest) request).getRequest();
            return String.format("%s %s", requestHttp.getMethod(), requestHttp.getRequestURI());
        } else {
            return "";
        }
    }
}