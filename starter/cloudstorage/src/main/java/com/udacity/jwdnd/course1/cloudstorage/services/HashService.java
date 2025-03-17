package com.udacity.jwdnd.course1.cloudstorage.services;


import org.slf4j.*;
import org.springframework.stereotype.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;


@Service
public class HashService {

    public final Logger logger = LoggerFactory.getLogger(HashService.class);

    public String getHashedValue(String data, String salt) {
        byte[] hashedValue = null;

        int iterCount = 12288;
        int derivedKeyLength = 256;
        KeySpec spec = new PBEKeySpec(data.toCharArray(), salt.getBytes(), iterCount, derivedKeyLength * 8);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            hashedValue = factory.generateSecret(spec)
                                 .getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }

        return Base64.getEncoder()
                     .encodeToString(hashedValue);
    }

}
