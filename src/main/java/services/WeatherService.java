package services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Locale;

public class WeatherService {

    private static final String API_BASE_URL = "https://api.open-meteo.com/v1/forecast";

    // Inner class to hold parsed weather data
    public static class WeatherData {
        private double temperature;
        private String weatherDescription;

        public WeatherData(double temperature, String weatherDescription) {
            this.temperature = temperature;
            this.weatherDescription = weatherDescription;
        }

        public double getTemperature() {
            return temperature;
        }

        public String getWeatherDescription() {
            return weatherDescription;
        }
    }

    public WeatherData fetchWeatherData(double latitude, double longitude) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlString = String.format(Locale.US, "%s?latitude=%f&longitude=%f&current=temperature_2m,weather_code&timezone=auto",
                                        API_BASE_URL, latitude, longitude);
        
        System.out.println("Fetching weather data for coordinates - Latitude: " + latitude + ", Longitude: " + longitude);
        System.out.println("Weather API URL: " + urlString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Weather API Response Status: " + response.statusCode());
        System.out.println("Weather API Response Body: " + response.body());

        if (response.statusCode() == 200) {
            return parseWeatherResponse(response.body());
        } else {
            System.err.println("Error fetching weather data: " + response.statusCode());
            return null;
        }
    }

    private WeatherData parseWeatherResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode currentWeather = root.path("current");

            if (currentWeather.isMissingNode()) {
                System.err.println("Weather data not found in response. Full response: " + jsonResponse);
                return null;
            }

            double temperature = currentWeather.path("temperature_2m").asDouble();
            int weatherCode = currentWeather.path("weather_code").asInt();
            System.out.println("Successfully parsed weather data - Temperature: " + temperature + "°C, Weather Code: " + weatherCode);

            // Simple mapping of weather codes to descriptions (Open-Meteo uses WMO codes)
            String weatherDescription = getWeatherDescription(weatherCode);
            System.out.println("Weather description: " + weatherDescription);

            return new WeatherData(temperature, weatherDescription);

        } catch (IOException e) {
            System.err.println("Error parsing weather data: " + e.getMessage());
            System.err.println("Raw response: " + jsonResponse);
            return null;
        }
    }

    // Basic mapping of WMO weather codes to descriptions
    private String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1, 2, 3 -> "Mostly clear to cloudy";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing Drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing Rain";
            case 71, 73, 75 -> "Snow fall";
            case 77 -> "Snow grains";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Unknown weather";
        };
    }
} 