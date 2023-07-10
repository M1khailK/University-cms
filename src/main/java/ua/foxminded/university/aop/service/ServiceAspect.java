package ua.foxminded.university.aop.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class ServiceAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAspect.class);

    @Pointcut(value = "execution(* ua.foxminded.university.services.impl.*.*(..) )")
    public void allServiceMethods() {
    }

    @Around("allServiceMethods()")
    public Object aroundServiceAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toString();
        logger.trace("Calling: {}", signature);
        Object object = joinPoint.proceed();
        if (!(object instanceof List)) {
            logger.trace("{} response: {}", signature, object);
        } else {
            logger.trace("{} completed successfully", signature);
        }
        return object;
    }
}
