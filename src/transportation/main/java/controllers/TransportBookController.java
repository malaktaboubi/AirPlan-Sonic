package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Transportation;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.layout.Region;
import java.util.stream.Collectors;


import javafx.util.StringConverter;
import org.json.JSONObject;
import services.ServiceTransportation;


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
    @FXML
    ScrollPane paneAffichage ;
    @FXML
    ScrollPane paneBook ;
    @FXML
    Label providerReservation;
    @FXML
    ImageView imageReservation;
    @FXML
    private ComboBox<LocalTime> timeComboBox; // or appropriate type

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

        gifView.setVisible(true);
        webView.setVisible(false);

        // Handle WebView load errors
        webView.getEngine().getLoadWorker().exceptionProperty().addListener((obs, old, ex) -> {
            if (ex != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load map: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Load transportation options
        List<Transportation> options;
        try {
            options = serviceTransportation.afficher();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load transportation options: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        listVbox.getChildren().clear();

        if (options.isEmpty()) {
            Label noResultsLabel = new Label("No transportation options found.");
            noResultsLabel.getStyleClass().add("no-results");
            listVbox.getChildren().add(noResultsLabel);
        } else {
            Platform.runLater(() -> {
                for (int i = 0; i < options.size(); i++) {
                    Transportation option = options.get(i);

                    VBox card;
                    try {
                        card = createTransportCard(option);
                    } catch (Exception e) {
                        System.err.println("Error creating card for transport ID " + option.getId() + ": " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }

                    card.setTranslateY(50);
                    card.setOpacity(0);
                    listVbox.getChildren().add(card);

                    // Animation setup
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), card);
                    ft.setFromValue(0);
                    ft.setToValue(1);

                    TranslateTransition tt = new TranslateTransition(Duration.millis(1000), card);
                    tt.setFromY(50);
                    tt.setToY(0);
                    tt.setInterpolator(Interpolator.EASE_OUT);

                    ScaleTransition st = new ScaleTransition(Duration.millis(300), card);
                    st.setFromX(0.9);
                    st.setToX(1);
                    st.setFromY(0.9);
                    st.setToY(1);
                    st.setCycleCount(2);
                    st.setAutoReverse(true);
                    st.setDelay(Duration.millis(400));

                    ParallelTransition pt = new ParallelTransition(ft, tt, st);
                    pt.setDelay(Duration.millis(i * 150)); // Staggered entrance
                    pt.play();
                }
            });
        }

        List<LocalTime> times = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(LocalTime.of(hour, minute));
            }
        }
        timeComboBox.setItems(FXCollections.observableArrayList(times));

        // Custom format the display to HH:mm
        timeComboBox.setConverter(new StringConverter<LocalTime>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            @Override
            public String toString(LocalTime time) {
                return (time != null) ? formatter.format(time) : "";
            }

            @Override
            public LocalTime fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalTime.parse(string, formatter) : null;
            }
        });

    }


    @FXML
    public void handleMap(ActionEvent event) {
        // Get user input
        String startLocation = startLocationField.getText().trim().toLowerCase();
        String destination = destinationField.getText().trim().toLowerCase();

        // Validate input
        if (startLocation.isEmpty() || destination.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter both starting location and destination.");
            alert.showAndWait();
            return;
        }

        // Show map and hide GIF
        gifView.setVisible(false);
        webView.setVisible(true);

        // Load map in WebView
        try {
            String encodedStart = URLEncoder.encode(startLocation, StandardCharsets.UTF_8);
            String encodedDest = URLEncoder.encode(destination, StandardCharsets.UTF_8);
            String mapUrl = String.format("https://www.google.com/maps/dir/%s/%s", encodedStart, encodedDest);
            webView.getEngine().load(mapUrl);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to construct map URL: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // FILTER and update transport cards using stream
        List<Transportation> allOptions = serviceTransportation.afficher();
        List<Transportation> filteredOptions = allOptions.stream()
                .filter(t -> t.getDeparturePoint().toLowerCase().contains(startLocation)
                        && t.getArrivalPoint().toLowerCase().contains(destination))
                .collect(Collectors.toList());

        // Update the card display
        listVbox.getChildren().clear();

        if (filteredOptions.isEmpty()) {
            Label noResults = new Label("No transportation found for this route.");
            noResults.getStyleClass().add("no-results");
            listVbox.getChildren().add(noResults);
        } else {
            for (int i = 0; i < filteredOptions.size(); i++) {
                Transportation option = filteredOptions.get(i);
                VBox card = createTransportCard(option);
                card.setTranslateY(50);
                card.setOpacity(0);
                listVbox.getChildren().add(card);

                // Animate cards as before
                ParallelTransition pt = new ParallelTransition();

                FadeTransition ft = new FadeTransition(Duration.millis(1000), card);
                ft.setFromValue(0);
                ft.setToValue(1);

                TranslateTransition tt = new TranslateTransition(Duration.millis(1000), card);
                tt.setFromY(50);
                tt.setToY(0);
                tt.setInterpolator(Interpolator.EASE_OUT);

                ScaleTransition st = new ScaleTransition(Duration.millis(300), card);
                st.setFromX(0.9);
                st.setToX(1);
                st.setFromY(0.9);
                st.setToY(1);
                st.setCycleCount(2);
                st.setAutoReverse(true);
                st.setDelay(Duration.millis(400));

                pt.getChildren().addAll(ft, tt, st);
                pt.setDelay(Duration.millis(i * 150));
                pt.play();
            }
        }
    }


    private VBox createTransportCard(Transportation option) {
        VBox card = new VBox(10);
        card.getStyleClass().add("transport-card");
        card.getStyleClass().add(option.getType().toLowerCase());
        card.setPadding(new Insets(10));
        card.setPrefWidth(350);

        // Title and icon
        Label typeLabel = new Label(capitalize(option.getType()) + " Service");
        typeLabel.getStyleClass().add("title");
        Node typeIcon = createTransportIcon(option.getType());

        HBox titleBox = new HBox(8, typeIcon, typeLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Image
        ImageView typeImage = new ImageView();
        typeImage.setFitHeight(60);
        typeImage.setFitWidth(60);

        String imagePath = switch (option.getType().toLowerCase()) {
            case "bus" -> "/images/bus.png";
            case "train" -> "/images/train.png";
            case "taxi" -> "/images/taxi.png";
            case "ship" -> "/images/ship.png";
            case "uber" -> "/images/uber.png";
            default -> "/images/location.png";
        };

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            typeImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        HBox headerBox = new HBox(10, titleBox, typeImage);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // First row: provider and departure time
        Label providerLabel = new Label(option.getProviderName());
        Label timeLabel = new Label("Departure: " + formatTime(option.getDepartureTime()));
        HBox row1 = new HBox(10, providerLabel, timeLabel);
        row1.setAlignment(Pos.CENTER_LEFT);

        // Second row: duration, price, and "Book" button on far right
        Label durationLabel = new Label("Duration: " + formatDuration(option.getDurationMinutes()));
        Label priceLabel = new Label("Price: $" + String.format("%.2f", option.getPrice()));

        Button bookButton = new Button("Book");
        bookButton.getStyleClass().add("book-button");
        bookButton.setVisible(false);

        // 🔁 On click: switch panes and fill info
        bookButton.setOnAction(e -> {
            // Set transport ID if needed elsewhere
            System.out.println("Book button clicked: " + option.getProviderName());
            int selectedTransportId = option.getId();

            // Switch panes
            //paneAffichage.setVisible(false);
            paneBook.setVisible(true);
            paneBook.toFront();

            // Set provider name
            providerReservation.setText(option.getProviderName());

            // Set image in booking pane
            imageReservation.setImage(typeImage.getImage());

            if (!option.getType().equalsIgnoreCase("bus") &&
                    !option.getType().equalsIgnoreCase("train") &&
                    !option.getType().equalsIgnoreCase("ship")) {

                timeComboBox.setDisable(false);
            }

        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Push button to the right

        HBox row2 = new HBox(10, durationLabel, priceLabel, spacer, bookButton);
        row2.setAlignment(Pos.CENTER_LEFT);

        // Available days below
        Label daysLabel = new Label("Available: " + formatOperatingDays(option.getOperatingDays()));
        HBox row3 = new HBox(daysLabel);
        row3.setAlignment(Pos.CENTER_LEFT);

        // Hover effect to show button
        card.setOnMouseEntered(e -> bookButton.setVisible(true));
        card.setOnMouseExited(e -> bookButton.setVisible(false));

        card.getChildren().addAll(headerBox, row1, row2, row3);
        card.getStyleClass().add("animated");
        card.setCache(true);

        return card;
    }

    private Node createTransportIcon(String type) {
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setSize("16");

        switch(type.toLowerCase()) {
            case "bus":
                icon.setGlyphName(String.valueOf(FontAwesomeIcon.BUS));
                icon.setStyle("-fx-fill: #3498db;");
                break;
            case "train":
                icon.setGlyphName(String.valueOf(FontAwesomeIcon.TRAIN));
                icon.setStyle("-fx-fill: #9b59b6;");
                break;
            case "taxi":
                icon.setGlyphName(String.valueOf(FontAwesomeIcon.TAXI));
                icon.setStyle("-fx-fill: #e67e22;");
                break;
            default:
                icon.setGlyphName(String.valueOf(FontAwesomeIcon.CAR));
        }
        return icon;
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
    //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

    public void handleCancelBooking(ActionEvent event){
        paneBook.setVisible(false);
    }

    public void handleBooking(ActionEvent actionEvent) {
    }

    public void handleReservations(ActionEvent actionEvent) {
    }
}