package com.projects.marketmosaic.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class ProductControllerAuditLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ProductControllerAuditLoggingAspect.class);

    @Before("execution(* com.projects.marketmosaic.controller.ProductController.*(..))")
    public void logRequest(JoinPoint joinPoint) {
        logger.info("Request to {} with arguments: {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "execution(* com.projects.marketmosaic.controller.ProductController.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        logger.info("Response from {}: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(value = "execution(* com.projects.marketmosaic.controller.ProductController.*(..))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        logger.error("Exception thrown in method {} with message: {}", joinPoint.getSignature(), exception.getMessage());
    }
}
