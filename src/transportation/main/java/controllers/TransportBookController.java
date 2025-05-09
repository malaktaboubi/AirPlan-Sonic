package controllers;

import entities.Transportation;
import javafx.animation.*;
import javafx.util.Duration;
import services.ServiceTransportation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;


public class TransportBookController {

    @FXML
    private ImageView gifView;

    @FXML
    private TextField startLocationField;

    @FXML
    private TextField destinationField;

    @FXML
    private WebView webView;

    @FXML
    private VBox listVbox;

    private ServiceTransportation serviceTransportation;

    @FXML
    private void initialize() {
        // Initialize service
        try {
            serviceTransportation = new ServiceTransportation();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to connect to database: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Ensure GIF is visible and WebView is hidden on startup
        gifView.setVisible(true);
        webView.setVisible(false);

        // Handle WebView load errors
        webView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, old, ex) -> {
            if (ex != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load map: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Load transportation options from database automatically
        List<Transportation> options;
        try {
            options = serviceTransportation.afficher();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load transportation options: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Clear previous cards (just in case)
        listVbox.getChildren().clear();

        // Add cards for each transport option
        if (options.isEmpty()) {
            Label noResultsLabel = new Label("No transportation options found.");
            noResultsLabel.getStyleClass().add("no-results");
            listVbox.getChildren().add(noResultsLabel);
        } else {
            // REPLACE THIS SECTION WITH ANIMATED VERSION
            for (int i = 0; i < options.size(); i++) {
                Transportation option = options.get(i);
                VBox card = createTransportCard(option);
                card.setTranslateY(50); // Start below final position
                card.setOpacity(0);
                listVbox.getChildren().add(card);

                ParallelTransition pt = new ParallelTransition();

                // Fade animation
                FadeTransition ft = new FadeTransition(Duration.millis(500), card);
                ft.setFromValue(0);
                ft.setToValue(1);

                // Slide animation
                TranslateTransition tt = new TranslateTransition(Duration.millis(600), card);
                tt.setFromY(50);
                tt.setToY(0);
                tt.setInterpolator(Interpolator.EASE_OUT);

                // Bounce effect
                ScaleTransition st = new ScaleTransition(Duration.millis(300), card);
                st.setFromX(0.9);
                st.setToX(1);
                st.setFromY(0.9);
                st.setToY(1);
                st.setCycleCount(2);
                st.setAutoReverse(true);
                st.setDelay(Duration.millis(400));

                pt.getChildren().addAll(ft, tt, st);
                pt.setDelay(Duration.millis(i * 150)); // Staggered delay
                pt.play();
            }
            // END OF ANIMATED SECTION
        }
    }

    @FXML
    public void handleMap(ActionEvent event) {
        // Get user input
        String startLocation = startLocationField.getText().trim();
        String destination = destinationField.getText().trim();

        // Validate input
        if (startLocation.isEmpty() || destination.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter both starting location and destination.");
            alert.showAndWait();
            return;
        }

        // Hide GIF and show WebView
        gifView.setVisible(false);
        webView.setVisible(true);

        // Load Google Maps with route
        try {
            String encodedStart = URLEncoder.encode(startLocation, StandardCharsets.UTF_8.toString());
            String encodedDest = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString());
            String mapUrl = String.format("https://www.google.com/maps/dir/%s/%s", encodedStart, encodedDest);
            webView.getEngine().load(mapUrl);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to construct map URL: " + e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    private VBox createTransportCard(Transportation option) {
        VBox card = new VBox(5);
        card.getStyleClass().add("transport-card");

        Label typeLabel = new Label("Type: " + capitalize(option.getType()));
        Label providerLabel = new Label("Provider: " + option.getProviderName());
        Label timeLabel = new Label("Departure: " + formatTime(option.getDepartureTime()));
        Label durationLabel = new Label("Duration: " + formatDuration(option.getDurationMinutes()));
        Label priceLabel = new Label("Price: $" + String.format("%.2f", option.getPrice()));
        Label daysLabel = new Label("Operating Days: " + formatOperatingDays(option.getOperatingDays()));

        card.getChildren().addAll(typeLabel, providerLabel, timeLabel, durationLabel, priceLabel, daysLabel);
        return card;
    }

    private String formatTime(LocalTime time) {
        if (time == null) return "N/A";
        return time.toString();
    }

    private String formatDuration(int minutes) {
        if (minutes <= 0) return "N/A";
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return (hours > 0 ? hours + "h " : "") + remainingMinutes + "m";
    }

    private String formatOperatingDays(String operatingDays) {
        if (operatingDays == null || operatingDays.length() != 7) return "N/A";
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < 7; i++) {
            if (operatingDays.charAt(i) == '1') {
                if (!first) result.append(", ");
                result.append(days[i]);
                first = false;
            }
        }
        return result.length() > 0 ? result.toString() : "None";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void handleGps(ActionEvent event) throws IOException {
        try {
        String apiUrl = "http://ip-api.com/json"; // IP-based geolocation API
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        JSONObject json = new JSONObject(response.toString());
        String city = json.getString("city");
        startLocationField.setText(city);

    } catch(
    Exception e)

    {
        e.printStackTrace();
        startLocationField.setText("Error getting location");
         }
    }
}