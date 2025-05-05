package com.example.ServiceClient.service;

import com.example.ServiceClient.dto.ClientLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor

public class GeocodingService {

    public ClientLocation geocode(String adresse) throws IOException {
        String encodedAddress = URLEncoder.encode(adresse, StandardCharsets.UTF_8);
        String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "YourAppName/1.0"); // Important !

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = in.lines().collect(Collectors.joining());
        in.close();

        // Log the full response to see if the geocoding API is returning results
        log.info("Geocoding API response: {}", response);

        JSONArray results = new JSONArray(response);
        if (results.length() > 0) {
            JSONObject first = results.getJSONObject(0);
            double lat = Double.parseDouble(first.getString("lat"));
            double lon = Double.parseDouble(first.getString("lon"));

            return new ClientLocation(adresse, lat, lon);
        }

        return new ClientLocation(adresse, null, null);
    }

}
