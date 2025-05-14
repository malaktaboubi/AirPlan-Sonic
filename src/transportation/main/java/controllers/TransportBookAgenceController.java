package controllers;

import entities.Reservation;
import entities.Transportation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import services.ServiceReservation;
import services.ServiceTransportation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TransportBookAgenceController {
    private ServiceReservation serviceReservation = new ServiceReservation();
    private ServiceTransportation serviceTransportation = new ServiceTransportation();
    @FXML
    private VBox listVbox;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Label pendingSum;
    @FXML
    private Label date;

    private List<Reservation> allReservations;

    public void initialize() {
        loadAndDisplayReservations();
        pendingSum.setText(String.valueOf(serviceReservation.pendingResCount()));
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date.setText(currentDate.format(formatter));
    }

    @FXML
    public void performSearch(ActionEvent actionEvent) {
        String searchText = searchField.getText().toLowerCase().trim();
        List<Reservation> filteredReservations;

        if (searchText.isEmpty()) {
            filteredReservations = allReservations;
        } else {
            filteredReservations = allReservations.stream()
                    .filter(reservation -> matchesSearch(reservation, searchText))
                    .collect(Collectors.toList());
        }

        // Update the UI with filtered results
        displayReservations(filteredReservations);
    }

    private void loadAndDisplayReservations() {
        try {
            allReservations = serviceReservation.afficher();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load reservations: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        displayReservations(allReservations);
    }

    private void displayReservations(List<Reservation> options) {
        listVbox.getChildren().clear();

        if (options.isEmpty()) {
            Label noResultsLabel = new Label("No Reservation options found.");
            noResultsLabel.getStyleClass().add("no-results");
            listVbox.getChildren().add(noResultsLabel);
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

    private boolean matchesSearch(Reservation reservation, String searchText) {
        return String.valueOf(reservation.getId()).contains(searchText) ||
                String.valueOf(reservation.getUserId()).contains(searchText) ||
                String.valueOf(reservation.getTransportId()).contains(searchText) ||
                reservation.getDeparturePoint().toLowerCase().contains(searchText) ||
                reservation.getArrivalPoint().toLowerCase().contains(searchText) ||
                reservation.getTravelDate().toString().contains(searchText) ||
                String.valueOf(reservation.getPassengers()).contains(searchText) ||
                reservation.getBookingTime().toString().toLowerCase().contains(searchText) ||
                reservation.getStatus().toLowerCase().contains(searchText);
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
        Label userLabel = new Label("User ID: " + reservation.getUserId());
        Label transportLabel = new Label("Transport ID: " + reservation.getTransportId());
        Label departureLabel = new Label("From: " + reservation.getDeparturePoint()); // Added departure point
        Label arrivalLabel = new Label("To: " + reservation.getArrivalPoint()); // Added arrival point
        Label dateLabel = new Label("Travel Date: " + reservation.getTravelDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Label passengersLabel = new Label("Passengers: " + reservation.getPassengers());
        Label bookingTimeLabel = new Label("Booking Time: " + reservation.getBookingTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Buttons (only for pending status)
        Button acceptButton = new Button("Accept");
        Button declineButton = new Button("Decline");
        acceptButton.getStyleClass().add("accept-button");
        declineButton.getStyleClass().add("decline-button");
        acceptButton.setVisible(false);
        declineButton.setVisible(false);

        if ("pending".equalsIgnoreCase(reservation.getStatus())) {
            acceptButton.setVisible(true);
            declineButton.setVisible(true);
        }

        // Buttons box
        HBox buttonBox = new HBox(10, acceptButton, declineButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox mainRow = new HBox(10,
                statusCircle,
                userLabel, transportLabel,
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
            if ("pending".equalsIgnoreCase(reservation.getStatus()) && !isEditing.get()) {
                acceptButton.setVisible(true);
                declineButton.setVisible(true);
            }
        });
        card.setOnMouseExited(e -> {
            if ("pending".equalsIgnoreCase(reservation.getStatus()) && !isEditing.get()) {
                acceptButton.setVisible(false);
                declineButton.setVisible(false);
            }
        });

        // Accept action (change status to confirmed)
        acceptButton.setOnAction(e -> {
            reservation.setStatus("confirmed");
            statusCircle.setFill(Color.GREEN);
            serviceReservation.modifier(reservation);
            System.out.println("Reservation " + reservation.getId() + " accepted.");
            acceptButton.setVisible(false);
            declineButton.setVisible(false);
        });

        // Decline action (change status to cancelled)
        declineButton.setOnAction(e -> {
            reservation.setStatus("cancelled");
            statusCircle.setFill(Color.RED);
            serviceReservation.modifier(reservation);
            System.out.println("Reservation " + reservation.getId() + " declined.");
            acceptButton.setVisible(false);
            declineButton.setVisible(false);
        });

        card.getStyleClass().add("animated");
        card.setCache(true);

        return card;
    }

    // Helper method to get color based on status
    private Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
                return Color.GREEN;
            case "cancelled":
                return Color.RED;
            case "pending":
                return Color.GRAY;
            default:
                return Color.GRAY;
        }
    }

    // Helper method to format time
    private String formatTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}