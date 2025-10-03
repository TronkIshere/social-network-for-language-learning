package com.private_project.charitable_money_management.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationException extends RuntimeException{
    private ErrorCode errorCode;
    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, Object... formatArgs) {
        super(errorCode.formatMessage(formatArgs));
        this.errorCode = errorCode;
    }
}

