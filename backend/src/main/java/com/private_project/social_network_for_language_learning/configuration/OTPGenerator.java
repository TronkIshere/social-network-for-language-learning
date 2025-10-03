package com.private_project.social_network_for_language_learning.configuration;

import java.security.SecureRandom;

public class OTPGenerator {
    public static String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

