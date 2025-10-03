package com.private_project.charitable_money_management.service;

public interface OTPService {

    boolean verifyOTP(String email, String otp);

    void sendOTP(String email);
}

