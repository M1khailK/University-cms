package ua.foxminded.university.customexceptions.handler;

import org.springframework.web.servlet.ModelAndView;

public interface CustomExceptionHandler<T> {

    ModelAndView handleCustomException(T exception);
}
