package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class ErrorResponse {

    private Timestamp timestamp;
    private int status;
    private List<String> errors;

    public ErrorResponse(int status, List<String> errors) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = status;
        this.errors = errors;
    }
}
