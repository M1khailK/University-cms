package ua.foxminded.university.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid date range")
public class InvalidDateRangeException extends RuntimeException{
    public InvalidDateRangeException(String message) {
        super(message);
    }

}
