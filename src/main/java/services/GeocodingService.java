package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeocodingService {

    private static final String API_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search";

    // Inner class to hold parsed geocoding data
    public static class Coordinates {
        private double latitude;
        private double longitude;

        public Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public Coordinates getCoordinates(String locationName) throws IOException, InterruptedException {
        if (locationName == null || locationName.trim().isEmpty()) {
            System.err.println("Error: Location name is null or empty");
            return null;
        }

        HttpClient client = HttpClient.newHttpClient();
        String encodedLocation = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString());
        String urlString = String.format("%s?name=%s&count=1", API_BASE_URL, encodedLocation);
        
        System.out.println("Fetching coordinates for location: " + locationName);
        System.out.println("API URL: " + urlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("API Response Status: " + response.statusCode());
        System.out.println("API Response Body: " + response.body());

        if (response.statusCode() == 200) {
            return parseGeocodingResponse(response.body());
        } else {
            System.err.println("Error fetching geocoding data: " + response.statusCode());
            return null;
        }
    }

    private Coordinates parseGeocodingResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode results = root.path("results");

            if (results.isArray() && results.size() > 0) {
                JsonNode firstResult = results.get(0);
                double latitude = firstResult.path("latitude").asDouble();
                double longitude = firstResult.path("longitude").asDouble();
                System.out.println("Found coordinates - Latitude: " + latitude + ", Longitude: " + longitude);
                return new Coordinates(latitude, longitude);
            } else {
                System.err.println("No geocoding results found in response: " + jsonResponse);
                return null;
            }

        } catch (IOException e) {
            System.err.println("Error parsing geocoding data: " + e.getMessage());
            System.err.println("Raw response: " + jsonResponse);
            return null;
        }
    }
} 