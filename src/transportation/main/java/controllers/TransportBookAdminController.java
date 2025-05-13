package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.util.Duration;
import services.ServiceCharts;
import services.ServiceReservation;
import services.ServiceTransportation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TransportBookAdminController {

    ServiceTransportation serviceTransportation = new ServiceTransportation();
    ServiceReservation serviceReservation = new ServiceReservation();
    ServiceCharts Servicecharts = new ServiceCharts();
    @FXML private VBox chatContainer;
    @FXML private TextField inputField;
    @FXML private ScrollPane scrollPane;
    @FXML private Label topService;
    @FXML private Label resNum;
    @FXML private Label date;
    @FXML private Label canceledRes;
    @FXML private PieChart pieChartType;
    @FXML public VBox listVbox;

    private TransportChatbot chatbot = new TransportChatbot();

    public void initialize() {
        // Initial bot message
        addMessage("Bot", "Any new transportation to be added? (yes/no)", false);

        // Auto-scroll to bottom when new messages are added
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setVvalue(1.0);
        });

        //numbers
        topService.setText(serviceTransportation.getMostFrequentProvider());
        resNum.setText(String.valueOf(serviceReservation.getReservationTotal()));
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date.setText(currentDate.format(formatter));
        canceledRes.setText(String.valueOf(serviceReservation.canceledResCount()));

        //chartpart
        pieChartType.setData(Servicecharts.getTransportTypeStats());

        //cardsssss
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


    }

    @FXML
    private void handleSend() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;

        addMessage("You", userInput, true);
        inputField.clear();

        String botResponse = chatbot.processInput(userInput);
        addMessage("Bot", botResponse, false);
    }

    private void addMessage(String sender, String text, boolean isUser) {
        // Create message container
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageContainer.setPadding(new Insets(5, 10, 5, 10));

        // Create message bubble
        VBox messageBubble = new VBox();
        messageBubble.getStyleClass().add("message-bubble");
        messageBubble.getStyleClass().add(isUser ? "user-message" : "bot-message");

        // Add sender label (only for bot messages)
        if (!isUser) {
            Label senderLabel = new Label(sender);
            senderLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");
            messageBubble.getChildren().add(senderLabel);
        }

        // Add message text
        Text messageText = new Text(text);
        messageText.setWrappingWidth(250);
        messageBubble.getChildren().add(messageText);

        // Add timestamp
        Label timeLabel = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("message-time");
        timeLabel.setPadding(new Insets(5, 0, 0, 0));
        messageBubble.getChildren().add(timeLabel);

        messageContainer.getChildren().add(messageBubble);
        chatContainer.getChildren().add(messageContainer);
    }

    //8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
    //cards
    private VBox createTransportCard(Transportation option) {
        final BooleanProperty isEditing = new SimpleBooleanProperty(false);

        VBox card = new VBox();
        card.getStyleClass().add("transport-card");
        card.getStyleClass().add(option.getType().toLowerCase());
        card.setPadding(new Insets(10));
        card.setPrefWidth(650);
        card.setPrefHeight(70);

        // UI elements for display
        Label typeLabel = new Label(capitalize(option.getType()) + " Service");
        typeLabel.getStyleClass().add("title");
        Node typeIcon = createTransportIcon(option.getType());
        ImageView typeImage = new ImageView();
        typeImage.setFitHeight(40);
        typeImage.setFitWidth(40);

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

        Label providerLabel = new Label(option.getProviderName());
        Label timeLabel = new Label("Departure: " + formatTime(option.getDepartureTime()));
        Label durationLabel = new Label("Duration: " + formatDuration(option.getDurationMinutes()));
        Label priceLabel = new Label("Price: $" + String.format("%.2f", option.getPrice()));

        Button deleteButton = new Button("Delete");
        Button modifyButton = new Button("Modify");
        deleteButton.getStyleClass().add("delete-button");
        modifyButton.getStyleClass().add("modify-button");
        deleteButton.setVisible(false);
        modifyButton.setVisible(false);

        // Buttons box
        HBox buttonBox = new HBox(10, modifyButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Static content layout
        HBox titleBox = new HBox(8, typeIcon, typeLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox mainRow = new HBox(10,
                typeImage, titleBox,
                providerLabel, timeLabel,
                durationLabel, priceLabel,
                spacer, buttonBox
        );
        mainRow.setAlignment(Pos.CENTER_LEFT);

        VBox displayBox = new VBox(mainRow);

        // Add display to card initially
        card.getChildren().add(displayBox);

        // Hover logic
        card.setOnMouseEntered(e -> {
            if (!isEditing.get()) {
                deleteButton.setVisible(true);
                modifyButton.setVisible(true);
            }
        });
        card.setOnMouseExited(e -> {
            if (!isEditing.get()) {
                deleteButton.setVisible(false);
                modifyButton.setVisible(false);
            }
        });

        // Delete action
        deleteButton.setOnAction(e -> {
            System.out.println("Delete clicked for: " + option.getProviderName());
            serviceTransportation.supprimer(option.getId());
            ((VBox) card.getParent()).getChildren().remove(card);
        });

        // Modify action
        modifyButton.setOnAction(e -> {
            isEditing.set(true);
            deleteButton.setVisible(false);
            modifyButton.setVisible(false);
            card.getChildren().clear();

            // Editable fields
            TextField priceField = new TextField(String.valueOf(option.getPrice()));
            TextField timeField = new TextField(option.getDepartureTime().toString());
            TextField daysField = new TextField(option.getOperatingDays());
            Button saveButton = new Button("Save");
            saveButton.getStyleClass().add("saveButton");

            VBox editBox = new VBox(8,
                    new Label("Price:"), priceField,
                    new Label("Departure Time:"), timeField,
                    new Label("Operating Days:"), daysField,
                    saveButton
            );
            editBox.setPadding(new Insets(10));

            card.getChildren().add(editBox);

            saveButton.setOnAction(ev -> {
                try {
                    double newPrice = Double.parseDouble(priceField.getText());
                    LocalTime newTime = LocalTime.parse(timeField.getText());
                    String newDays = daysField.getText();

                    if (newDays.length() != 7 || !newDays.matches("[01]+")) {
                        throw new IllegalArgumentException("Operating days must be 7 digits (0/1).");
                    }

                    // Update object
                    option.setPrice(newPrice);
                    option.setDepartureTime(newTime);
                    option.setOperatingDays(newDays);

                    // Call your service to update DB
                    serviceTransportation.modifier(option);

                    // Reset to non-edit mode
                    isEditing.set(false);

                    // Update labels
                    timeLabel.setText("Departure: " + formatTime(newTime));
                    priceLabel.setText("Price: $" + String.format("%.2f", newPrice));

                    // Reset view
                    card.getChildren().clear();
                    card.getChildren().add(displayBox);
                } catch (Exception ex) {
                    System.err.println("Error updating: " + ex.getMessage());
                }
            });
        });

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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
