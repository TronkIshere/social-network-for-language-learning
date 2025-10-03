package com.private_project.social_network_for_language_learning.service;

public interface OTPService {

    boolean verifyOTP(String email, String otp);

    void sendOTP(String email);
}

