package com.private_project.charitable_money_management.configuration;

import java.security.SecureRandom;

public class OTPGenerator {
    public static String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

