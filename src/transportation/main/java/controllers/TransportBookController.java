package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.Transportation;
import entities.TransportationPlusHistory;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import entities.Reservation;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONObject;
import services.ServiceTransportation;
import services.ServiceTransportationPlusHistory;
import javafx.util.StringConverter;
import services.ServiceReservation;

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
    private ScrollPane paneAffichage;
    @FXML
    private ScrollPane paneBook;
    @FXML
    private Label providerReservation;
    @FXML
    private ImageView imageReservation;
    @FXML
    private ComboBox<LocalTime> timeComboBox;
    @FXML
    private Button chartButton;
    @FXML
    private BarChart<String, Number> chartclient; // Added BarChart injection
    @FXML
    private Spinner<Integer> passengersNum;
    @FXML
    private DatePicker date;
    private int selectedTransportId = -1;
    private List<Reservation> allReservations;
    @FXML
    VBox reservationList;
    @FXML
    ScrollPane paneAffichage1;



    private ServiceTransportation serviceTransportation;
    private ServiceTransportationPlusHistory serviceHistory; // Added for history data
    private final ServiceReservation serviceReservation=new ServiceReservation();

    @FXML
    private void initialize() {

        paneBook.setVisible(false);
        paneAffichage.setVisible(true);
        paneAffichage1.setVisible(false);

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1); // min, max, initial
        passengersNum.setValueFactory(valueFactory);


        // Initialize services
        try {
            serviceTransportation = new ServiceTransportation();
            serviceHistory = new ServiceTransportationPlusHistory(); // Initialize history service
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

        bookButton.setOnAction(e -> {
            // Set transport ID if needed elsewhere
            System.out.println("Book button clicked: " + option.getProviderName());
            selectedTransportId = option.getId();

            // Switch panes
            paneAffichage.setVisible(false); // Uncommented and enabled
            paneBook.setVisible(true);
            paneBook.toFront();

            LocalTime finalDepartureTime;

            if (!timeComboBox.isDisabled() && timeComboBox.getValue() != null) {
                // Use selected time from combo box
                finalDepartureTime = timeComboBox.getValue();
            } else {
                // Use default scheduled time from transport option
                finalDepartureTime = option.getDepartureTime();
            }


            System.out.println("Final departure time used: " + finalDepartureTime);

            // Set provider name
            providerReservation.setText(option.getProviderName());

            // Set image in booking pane
            imageReservation.setImage(typeImage.getImage());

            // Enable timeComboBox for non-scheduled transports
            if (!option.getType().equalsIgnoreCase("bus") &&
                    !option.getType().equalsIgnoreCase("train") &&
                    !option.getType().equalsIgnoreCase("ship")) {
                timeComboBox.setDisable(false);
            } else {
                timeComboBox.setDisable(true); // Disable for scheduled transports
            }
            timeComboBox.setValue(option.getDepartureTime());


            // Update chart with historical data for the selected transport
            int transportId = selectedTransportId; // Use the transport ID from the selected option
            List<TransportationPlusHistory> history;
            try {
                history = serviceHistory.getHistoryByTransportId(transportId);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load history data: " + ex.getMessage());
                alert.showAndWait();
                return;
            }

            // Clear existing data
            chartclient.getData().clear();

            // Prepare series data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Passengers per Day (Transport ID: " + transportId + ")");

            if (history.isEmpty()) {
                series.getData().add(new XYChart.Data<>("No Data", 0));
            } else {
                for (TransportationPlusHistory record : history) {
                    series.getData().add(new XYChart.Data<>(record.getRecordedDate().toString(), record.getPassengersSumPerDay()));
                }
            }

            // Add series to chart
            chartclient.getData().add(series);

            // Set chart title
            chartclient.setTitle("Passenger Trends for Transport ID: " + transportId);
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

        switch (type.toLowerCase()) {
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

        } catch (Exception e) {
            e.printStackTrace();
            startLocationField.setText("Error getting location");
        }
    }

    @FXML
    private void handleBooking(ActionEvent event) {
        if (providerReservation.getText() == null || providerReservation.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Booking Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a transport before booking.");
            alert.showAndWait();
            return;
        }

        // Get user input
        String provider = providerReservation.getText();
        LocalTime selectedTime = timeComboBox.isDisabled() ? null : timeComboBox.getValue();
        String departure = startLocationField.getText(); // Make sure these match your actual input field fx:id
        String arrival = destinationField.getText();
        LocalDate travelDate = date.getValue(); // uses the system's current date
        int passengers = passengersNum.getValue(); // add validation!
        LocalDateTime bookingTime = LocalDateTime.now();

        // Example: get transportId (assume you store it when a user clicks "Book")
        int transportId = selectedTransportId;
        //int userId = currentUser.getId(); // You need a reference to the logged-in user

        // Create the reservation
        Reservation reservation = new Reservation(
                9,
                transportId,
                departure,
                arrival,
                travelDate,
                passengers,
                bookingTime,
                "pending"
        );

        // Save to DB
        try {
            serviceReservation.ajouter(reservation);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Booking Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not save your booking: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Switch panes and show confirmation
        paneBook.setVisible(false);
        paneAffichage.setVisible(true);
        paneAffichage.toFront();

        Alert confirm = new Alert(Alert.AlertType.INFORMATION);
        confirm.setTitle("Booking Confirmed");
        confirm.setHeaderText(null);
        confirm.setContentText("Your booking with " + provider +
                (selectedTime != null ? " at " + selectedTime.toString() : "") + " is confirmed.");
        confirm.showAndWait();
    }



    public void handleCancelBooking(ActionEvent event) {
        paneBook.setVisible(false);
        paneAffichage.setVisible(true); // Return to display pane
    }


    public void handleReservations(ActionEvent actionEvent) {
        // Implement reservations display logic here
        System.out.println("Reservations view triggered");
        paneBook.setVisible(false);
        paneAffichage.setVisible(false);
        paneAffichage1.setVisible(true);
        loadAndDisplayReservations();
    }

    private void loadAndDisplayReservations() {
        try {
            allReservations = serviceReservation.afficherForUser(9);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load reservations: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        displayReservations(allReservations);
    }

    private void displayReservations(List<Reservation> options) {
        reservationList.getChildren().clear();

        if (options.isEmpty()) {
            Label noResultsLabel = new Label("No Reservation options found.");
            noResultsLabel.getStyleClass().add("no-results");
            reservationList.getChildren().add(noResultsLabel);
        } else {
            Platform.runLater(() -> {
                for (int i = 0; i < options.size(); i++) {
                    Reservation option = options.get(i);

                    VBox card;
                    try {
                        card = createReservationCard(option);
                    } catch (Exception e) {
                        System.err.println("Error creating card for reservation ID " + option.getId() + ": " + e.getMessage());
                        e.printStackTrace();
                        continue;
                    }

                    card.setTranslateY(50);
                    card.setOpacity(0);
                    reservationList.getChildren().add(card);

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
                    st.setFromY(0.9);
                    st.setToX(1);
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
    }

    private VBox createReservationCard(Reservation reservation) {
        final BooleanProperty isEditing = new SimpleBooleanProperty(false);

        VBox card = new VBox();
        card.getStyleClass().add("reservation-card");
        card.setPadding(new Insets(10));
        card.setPrefWidth(750);
        card.setPrefHeight(70);

        // Status circle
        Circle statusCircle = new Circle(20);
        statusCircle.setStroke(Color.BLACK);
        statusCircle.setStrokeWidth(1);
        switch (reservation.getStatus().toLowerCase()) {
            case "confirmed":
                statusCircle.setFill(Color.GREEN);
                break;
            case "cancelled":
                statusCircle.setFill(Color.RED);
                break;
            case "pending":
                statusCircle.setFill(Color.GRAY);
                break;
            default:
                statusCircle.setFill(Color.GRAY);
        }

        // UI elements for display
        Label transportLabel = new Label("Transport ID: " + reservation.getTransportId());
        Label departureLabel = new Label("From: " + reservation.getDeparturePoint()); // Added departure point
        Label arrivalLabel = new Label("To: " + reservation.getArrivalPoint()); // Added arrival point
        Label dateLabel = new Label("Travel Date: " + reservation.getTravelDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Label passengersLabel = new Label("Passengers: " + reservation.getPassengers());
        Label bookingTimeLabel = new Label("Booking Time: " + reservation.getBookingTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        //not for canceled reservations
        Button declineButton = new Button("❎");
        declineButton.getStyleClass().add("decline-button");
        declineButton.setVisible(false);

        if (LocalDate.now().compareTo(reservation.getTravelDate()) < 0
            && !Objects.equals(reservation.getStatus(), "cancelled")) {
            declineButton.setVisible(true);
        }

        // Buttons box
        HBox buttonBox = new HBox(10, declineButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox mainRow = new HBox(10,
                statusCircle,
                transportLabel,
                departureLabel, arrivalLabel, // Added to layout
                dateLabel, passengersLabel,
                bookingTimeLabel,
                spacer, buttonBox
        );
        mainRow.setAlignment(Pos.CENTER_LEFT);

        VBox displayBox = new VBox(mainRow);

        // Add display to card initially
        card.getChildren().add(displayBox);

        // Hover logic (only for pending status)
        card.setOnMouseEntered(e -> {
            if (LocalDate.now().compareTo(reservation.getTravelDate()) < 0) {
                declineButton.setVisible(true);
            }
        });
        card.setOnMouseExited(e -> {
            if (LocalDate.now().compareTo(reservation.getTravelDate()) < 0) {
                declineButton.setVisible(false);
            }
        });

        // Decline action (change status to cancelled)
        declineButton.setOnAction(e -> {
            reservation.setStatus("cancelled");
            statusCircle.setFill(Color.RED);
            serviceReservation.modifier(reservation);
            System.out.println("Reservation " + reservation.getId() + " declined.");
            //acceptButton.setVisible(false);
            declineButton.setVisible(false);
        });

        card.getStyleClass().add("animated");
        card.setCache(true);

        return card;
    }
}