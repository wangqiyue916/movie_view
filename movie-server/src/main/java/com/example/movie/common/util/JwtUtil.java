package com.example.movie.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "movie-review-system-secret-key-2024";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    public static String generateToken(Long userId, String username, List<String> roles) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("username", username);
            payload.put("roles", roles);
            long now = System.currentTimeMillis();
            payload.put("iat", now / 1000);
            payload.put("exp", (now + EXPIRATION_MS) / 1000);

            String headerB64 = base64UrlEncode(MAPPER.writeValueAsBytes(header));
            String payloadB64 = base64UrlEncode(MAPPER.writeValueAsBytes(payload));
            String signature = sign(headerB64 + "." + payloadB64);

            return headerB64 + "." + payloadB64 + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("JWT生成失败", e);
        }
    }

    public static Map<String, Object> parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new RuntimeException("无效的Token格式");

            String unsigned = parts[0] + "." + parts[1];
            String expectedSig = sign(unsigned);
            if (!expectedSig.equals(parts[2])) throw new RuntimeException("Token签名验证失败");

            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            return MAPPER.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Token解析失败", e);
        }
    }

    public static boolean isTokenExpired(Map<String, Object> claims) {
        Object exp = claims.get("exp");
        if (exp == null) return true;
        long expTime = exp instanceof Long ? (Long) exp : ((Number) exp).longValue();
        return expTime * 1000 < System.currentTimeMillis();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Map<String, Object> claims) {
        return (List<String>) claims.get("roles");
    }

    public static Long getUserId(Map<String, Object> claims) {
        Object uid = claims.get("userId");
        return uid instanceof Long ? (Long) uid : ((Number) uid).longValue();
    }

    public static String getUsername(Map<String, Object> claims) {
        return (String) claims.get("username");
    }

    private static String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return base64UrlEncode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }
}