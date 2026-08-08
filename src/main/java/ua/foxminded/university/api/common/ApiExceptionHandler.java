package ua.foxminded.university.api.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.foxminded.university.customexceptions.GroupNotFoundException;
import ua.foxminded.university.customexceptions.SubjectNotFoundException;

@RestControllerAdvice(basePackages = "ua.foxminded.university.api")
public class ApiExceptionHandler {

    @ExceptionHandler(SubjectNotFoundException.class)
    public ProblemDetail handleSubjectNotFound(SubjectNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Subject not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ProblemDetail handleGroupNotFound(GroupNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Group not found");
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}