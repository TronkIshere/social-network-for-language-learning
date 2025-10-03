package com.private_project.social_network_for_language_learning.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 4xx Client Errors (400-499)
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),

    // Token related errors (grouped together)
    TOKEN_INVALID(4001, "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(4002, "Token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLISTED(4003, "Token is blacklisted", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID(4004, "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(4005, "Refresh token expired", HttpStatus.UNAUTHORIZED),
    JWT_SECRET_NOT_CONFIGURED(4006, "JWT secret not configured", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication/Authorization
    SIGN_OUT_FAILED(4010, "Sign out failed", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(4011, "Invalid credentials", HttpStatus.UNAUTHORIZED),

    // User related errors
    USER_NOT_EXISTED(4041, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXIST_EXCEPTION(4091, "User already exists", HttpStatus.CONFLICT),
    USER_ALREADY_HAS_THIS_ROLE(4092, "User already has this role", HttpStatus.CONFLICT),

    // Role related errors
    ROLE_ALREADY_EXIST_EXCEPTION(4093, "Role already exists", HttpStatus.CONFLICT),

    // Resource related errors
    RESOURCE_NOT_FOUND(4042, "Resource not found", HttpStatus.NOT_FOUND),
    ENTITY_IS_ALREADY_DELETED(4101, "Entity has been deleted", HttpStatus.GONE),

    // System/Generic errors
    INVALID_KEY(5001, "Invalid key", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_OTP(5002, "Invalid or expired OTP", HttpStatus.UNAUTHORIZED),

    EMAIL_NOT_FOUND(4042, "Email not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_HAS_LOCAL_ACCOUNT( 4042, "User already has local", HttpStatus.CONFLICT),
    INVALID_EMAIL(4042, "Invalid email", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS( 4042, "Email already exits", HttpStatus.CONFLICT),
    ACCOUNT_NOT_LINKED_WITH_LOCAL(4042, "Account not linked with local", HttpStatus.CONFLICT),

    // File related errors
    FILE_NOT_FOUND(5005, "File not found", HttpStatus.NOT_FOUND),
    FAIL_TO_PROCESS(5006, "Fail to process", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_CLOSING(5006, "Stream closing error", HttpStatus.INTERNAL_SERVER_ERROR),
    COLUMN_NOT_FOUND(5007, "Column not found: %s", HttpStatus.NOT_FOUND),
    FILE_READ_ERROR(5008, "Cannot read file", HttpStatus.INTERNAL_SERVER_ERROR),
    SYSTEM_ERROR(5009, "Unknown error: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TYPE_FILE(5010, "Invalid file: %s", HttpStatus.CONFLICT);

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }

    int code;
    String message;
    HttpStatus statusCode;
}
