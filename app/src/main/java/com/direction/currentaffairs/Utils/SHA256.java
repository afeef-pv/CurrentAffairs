package com.direction.currentaffairs.Utils;

import android.os.Build;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    public static String toHexString(String hashStr) throws NoSuchAlgorithmException {
        byte[] hash = getSHA(hashStr);
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
