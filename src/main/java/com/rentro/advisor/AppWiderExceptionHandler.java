package com.rentro.advisor;

import com.rentro.dto.response.StandardResponseDto;
import com.rentro.exception.DuplicateEntryException;
import com.rentro.exception.EntryNotFoundException;
import com.rentro.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWiderExceptionHandler {

    @ExceptionHandler(EntryNotFoundException.class)
    public ResponseEntity<StandardResponseDto> handleEntryNotFoundException(EntryNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardResponseDto.builder()
                        .code(404)
                        .message(ex.getMessage())
                        .data(ex)
                        .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<StandardResponseDto> handleValidationException(ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(StandardResponseDto.builder()
                        .code(400)
                        .message(ex.getMessage())
                        .data(ex)
                        .build());
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<StandardResponseDto> handleDuplicateEntryException(DuplicateEntryException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(StandardResponseDto.builder()
                        .code(409)
                        .message(ex.getMessage())
                        .data(ex)
                        .build());
    }

}
