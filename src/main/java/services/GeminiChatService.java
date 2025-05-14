package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiChatService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = "AIzaSyBTSXJMXvZodEZeyjXVaLDf1fgEmje4s3o"; // Replace with your Gemini API key
    public static String sendMessage(String message) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        // Build JSON request for Gemini API
        String json = "{"
                + "\"contents\": ["
                + "    {"
                + "        \"parts\": ["
                + "            {"
                + "                \"text\": \"" + message + "\""
                + "            }"
                + "        ]"
                + "    }"
                + "]"
                + "}";

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()

                .url(API_URL + "?key=" + API_KEY) // Append the API key as a query parameter
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            // Log the response code and headers for debugging
            System.out.println("Response Code: " + response.code());
            System.out.println("Response Headers: " + response.headers());
            if (!response.isSuccessful()) {
                // Log the raw response body for debugging
                String responseBody = response.body().string();
                System.out.println("Raw API Response: " + responseBody);
                throw new IOException("Unexpected response code: " + response.code());
            }

            // Parse JSON response
            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            // Extract the generated text from the response
            if (jsonResponse.has("candidates") && jsonResponse.get("candidates").isArray()
                    && jsonResponse.get("candidates").size() > 0) {
                JsonNode candidate = jsonResponse.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")) {
                    JsonNode parts = candidate.get("content").get("parts");
                    if (parts.isArray() && parts.size() > 0 && parts.get(0).has("text")) {
                        return parts.get(0).get("text").asText();
                    }
                }
            }

            // If the response structure is unexpected, return an error message
            return "Error: Invalid response from API → " + responseBody;
        }
    }
}