package com.evgeniyfedorchenko.hogwarts.exceptions;

public class AvatarProcessingException extends RuntimeException {

    public AvatarProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return "The data image is damaged or lost. Re-download required";
    }
}
