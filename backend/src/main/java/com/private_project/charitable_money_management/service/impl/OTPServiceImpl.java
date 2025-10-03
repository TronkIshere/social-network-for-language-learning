package com.private_project.charitable_money_management.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.private_project.charitable_money_management.configuration.OTPGenerator;
import com.private_project.charitable_money_management.service.EmailService;
import com.private_project.charitable_money_management.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private final EmailService emailService;
    private final Cache<String, String> otpCache = Caffeine.newBuilder()
            .expireAfterWrite(OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean verifyOTP(String email, String otp) {
        String cachedOtp = otpCache.getIfPresent(email);
        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            otpCache.invalidate(email);
            return false;
        }
        return true;
    }

    @Override
    public void sendOTP(String email) {
        String otp = OTPGenerator.generateOTP();
        otpCache.put(email, otp);
        emailService.sendOTP(email, otp);
    }
}

