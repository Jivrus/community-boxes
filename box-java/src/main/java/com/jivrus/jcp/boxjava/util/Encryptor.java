package com.jivrus.jcp.boxjava.util;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    private static final String DEFAULT_KEY = "DuolcMroftalpSurvij";
    private static final String COMPARE_KEY = "1581bb4b-c172-43a5-864d-07a27d77b3be";

    public static String encrypt(String algo, Map<String, String> options) throws Exception {
        switch (algo) {
            case "aes":
                return encryptAES(options);
            case "base64":
                return Base64.getEncoder().encodeToString(options.get("text").getBytes());
            case "buffer":
                return new String(Base64.getEncoder().encode(options.get("text").getBytes()));
            case "encodeuri":
                return URLEncoder.encode(options.get("text"), "UTF-8");
            case "formurlencoded":
                return ""; //TDOD - implement this when required
            default:
                throw new IllegalArgumentException("Unsupported encryption algorithm: " + algo);
        }
    }

    public static String decrypt(String algo, Map<String, String> options) throws Exception {
        switch (algo) {
            case "aes":
                return decryptAES(options);
            default:
                throw new IllegalArgumentException("Unsupported decryption algorithm: " + algo);
        }
    }

    public static Map<String, Object> verify(String algo, Map<String, String> options) throws Exception {
        switch (algo) {
            case "jwtstateless":
                return verifyJWTStateless(options);
            default:
                throw new IllegalArgumentException("Unsupported verification algorithm: " + algo);
        }
    }

    private static String encryptAES(Map<String, String> options) throws Exception {
        String text = options.get("text");
        String key = options.getOrDefault("key", DEFAULT_KEY);
        key = key.substring(0, 16); //TODO - currently used 16 bytes
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        
        
        byte [] bytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decryptAES(Map<String, String> options) throws Exception {
        String ciphertext = options.get("cipherText");
        String key = options.getOrDefault("key", DEFAULT_KEY);
        key = key.substring(0, 16); //TODO - currently used 16 bytes
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        return new String(cipher.doFinal(encryptedBytes));
    }

    private static Map<String, Object> verifyJWTStateless(Map<String, String> options) throws Exception {
        String token = options.get("text");
        Map<String, Object> result = new HashMap<>();

        try {
//            Jwts.parser()
//                .setSigningKeyResolver(claims ->
//                    claims.get("key").equals(COMPARE_KEY) ? DEFAULT_KEY : null
//                )
//                .parseClaimsJws(token);
            result.put("verified", true);
        } catch (Exception e) {
            result.put("error", e.getMessage());
            throw e;
        }

        return result;
    }
    
    public static void main(String[] args) throws Exception {
        Map map = Map.of("text", "{\"host\": \"test\"}");

        String cipher = Encryptor.encryptAES(map);
        System.out.println("Encrypted string: " + cipher);

        map = Map.of("cipherText", cipher);
        String text = Encryptor.decryptAES(map);
        System.out.println("Decrypted string: " + text);
    }
}
