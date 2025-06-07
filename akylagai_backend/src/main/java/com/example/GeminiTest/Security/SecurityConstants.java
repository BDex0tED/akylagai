package com.example.GeminiTest.Security;


import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SecurityConstants {
    public static final long JWT_EXPIRATION_TIME = 86400000;
    public static final long JWT_REFRESH_EXPIRATION_TIME = 2592000000L;
    public static final String JWT_SECRET = "secret";

    public static final Key key = new SecretKeySpec(JWT_SECRET.getBytes(), SignatureAlgorithm.HS512.getJcaName());


}
