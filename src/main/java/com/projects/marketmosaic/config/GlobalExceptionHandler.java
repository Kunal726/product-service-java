package com.projects.marketmosaic.config;

import com.projects.marketmosaic.dtos.BaseRespDTO;
import com.projects.marketmosaic.exception.ProductException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<BaseRespDTO> handleProductException(ProductException e) {
        BaseRespDTO respDTO = new BaseRespDTO();
        respDTO.setStatus(false);
        respDTO.setCode(e.getErrorCode());
        respDTO.setMessage(e.getMessage());
        return new ResponseEntity<>(respDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseRespDTO> handleGenericException(Exception e) {
        BaseRespDTO respDTO = new BaseRespDTO();
        respDTO.setStatus(false);
        respDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        respDTO.setMessage("An unexpected error occurred");
        return new ResponseEntity<>(respDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<BaseRespDTO> handleValidationExceptions(MethodArgumentNotValidException argumentNotValidException) {
        BindingResult bindingResult = argumentNotValidException.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        BaseRespDTO errorResponse = new BaseRespDTO();
        errorResponse.setStatus(false);
        errorResponse.setCode(400);
        errorResponse.setMessage("Validation failed for provided fields.");
        errorResponse.setAdditionalInfo(validationErrors.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}

