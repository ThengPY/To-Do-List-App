import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EmbeddingGenerator {
    private static final String API_URL = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";
    private static final String API_TOKEN = "hf_your_api_token_here"; // Replace "hf_your_api_token_here" with your actual Hugging Face API token.
   
    private static final Map<String, double[]> embeddingCache = new HashMap<>();

    public static double[] generateEmbedding(String text) throws IOException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be null or empty");
        }

        if (embeddingCache.containsKey(text)) {
            return embeddingCache.get(text);
        }

        // Create the HTTP request
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000); // 5 seconds
        connection.setReadTimeout(5000);    // 5 seconds
        connection.setDoOutput(true);

        // Create the JSON payload
        JsonObject json = new JsonObject();
        json.addProperty("inputs", text);
        String jsonInputString = json.toString();

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Check for HTTP errors
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            try (InputStream errorStream = connection.getErrorStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, "utf-8"))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                throw new IOException("API request failed with code " + responseCode + ": " + errorResponse.toString());
            }
        }

        // Read the response
        try (InputStream is = connection.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Parse the JSON response
            JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
            double[] embedding = new double[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                embedding[i] = jsonArray.get(i).getAsDouble();
            }

            // Cache the embedding
            embeddingCache.put(text, embedding);
            return embedding;
        }
    }
}