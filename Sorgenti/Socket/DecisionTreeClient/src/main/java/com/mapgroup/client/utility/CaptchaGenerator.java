package com.mapgroup.client.utility;

import java.util.Random;

/**
 * This class presents a simple random alphanumeric string
 * generator.
 */
public class CaptchaGenerator {

    /** All the characters that could be generated */
    private static final char[] values = new char[62];
    /** random number generator for the characters array */
    private static final Random randValues = new Random(61);

    static {

        randValues.setSeed(new java.util.Date().getTime());

        int index = 0;

        for (char a = '0'; a <= '9'; a++)
            values[index++] = a;

        for (char a = 'A'; a <= 'Z'; a++)
            values[index++] = a;

        for (char a = 'a'; a <= 'z'; a++)
            values[index++] = a;

    }

    private CaptchaGenerator() {
    }

    /**
     * Generates a random alphanumeric 5-characters string using the
     * array of characters.
     *
     * @return the random string generated
     */
    public static String generateCaptcha() {
        String captcha = "";

        for (int i = 0; i < 5; i++) {
            captcha += values[Math.abs(randValues.nextInt(61))];

        }

        return captcha;
    }
}
