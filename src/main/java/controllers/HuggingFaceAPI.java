package controllers;



import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HuggingFaceAPI {

    private static final String API_URL = "https://api-inference.huggingface.co/models/distilbert-base-uncased-finetuned-sst-2-english";
    private static final String TEXT_TO_IMAGE_API_URL = "https://api-inference.huggingface.co/models/stabilityai/stable-diffusion-2-1";

    private static final String API_TOKEN = "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"; // Replace with your Hugging Face API token

    public String analyzeSentiment(String text) {
        try {
            // Prepare the JSON payload
            JsonObject payload = new JsonObject();
            payload.addProperty("inputs", text);

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            // Send the request and get the response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the response as a JSON array
            JsonArray responseArray = JsonParser.parseString(response.body()).getAsJsonArray();

            // Extract the first element (which contains the sentiment analysis result)
            JsonObject firstElement = responseArray.get(0).getAsJsonArray().get(0).getAsJsonObject();
            String sentiment = firstElement.get("label").getAsString();

            return sentiment; // Returns "POSITIVE" or "NEGATIVE"
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public String generateAutoResponse(String sentiment) {
        if (sentiment.equals("POSITIVE")) {
            return "Thank you for your positive feedback! We're glad to hear you're satisfied.";
        } else if (sentiment.equals("NEGATIVE")) {
            return "We're sorry to hear about your experience. Our team will look into this issue immediately.";
        } else {
            return "Thank you for your feedback. We will review your request shortly.";
        }
    }
    /**
     * Generate an image from text using Hugging Face Text-to-Image API.
     *
     * @param text The text to generate an image from.
     * @param outputFile The output file path (e.g., "output.png").
     * @return True if the image generation was successful, false otherwise.
     */
    public boolean generateImage(String text, String outputFile) {
        int retries = 3; // Number of retries
        int delay = 5000; // Delay between retries in milliseconds

        for (int i = 0; i < retries; i++) {
            try {
                // Prepare the JSON payload
                JsonObject payload = new JsonObject();
                payload.addProperty("inputs", text);

                // Create the HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(TEXT_TO_IMAGE_API_URL))
                        .header("Authorization", "Bearer " + API_TOKEN)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                // Send the request and get the response
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    // Save the image response to a file
                    byte[] imageBytes = response.body();
                    try (FileOutputStream out = new FileOutputStream(outputFile)) {
                        out.write(imageBytes);
                    }
                    return true; // Image generation successful
                } else if (response.statusCode() == 503) {
                    System.err.println("Attempt " + (i + 1) + ": API unavailable. Retrying in " + delay / 1000 + " seconds...");
                    Thread.sleep(delay); // Wait before retrying
                } else {
                    System.err.println("Image generation API request failed with status code: " + response.statusCode());
                    return false; // Image generation failed
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                return false; // Image generation failed
            }
        }
        System.err.println("All retry attempts failed. API is unavailable.");
        return false; // Image generation failed after retries
    }


}