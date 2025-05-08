package controllersEya;

import controllers.MenuController;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import modelsEya.Hebergement;
import modelsEya.Reservation;
import org.json.JSONObject;
import services.ControlledScreen;
import servicesEya.ServiceHebergement;
import servicesEya.ServiceReservation;
import view.AccCellBookedFactory;
import view.AccCellFavorisFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientAcc implements ControlledScreen {

    private MenuController mainController;

    @Override
    public void setMainController(MenuController mainController) {
        this.mainController = mainController;
    }

    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane flowPane;
    @FXML private ComboBox<String> pricecombo;
    @FXML private TextField destinationfiled;
    @FXML private WebView weatherWebView;
    @FXML private Button btnliked;
    @FXML private Button btnbooked;

    private boolean favoritesVisible = false;
    private VBox favoritesContainer;
    private ListView<Hebergement> favoritesListView;

    private ServiceHebergement service = new ServiceHebergement();
    private List<Hebergement> hotelList = new ArrayList<>();
    private List<Hebergement> displayedHotels = new ArrayList<>();

    private boolean bookedVisible = false;
    private VBox bookedContainer;
    private ListView<Reservation> bookedListView;

    private static final String OPENWEATHER_API_KEY = "054d682ef2cd7a8772bb51e1adfcdc70";
    private static final String OPENWEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    @FXML
    public void initialize() {
        setupUI();
        setupEventHandlers();
        loadHotels();
        btnliked.setOnAction(e -> toggleFavorites());
        btnbooked.setOnAction(e -> toggleBooked());
    }

    private void setupUI() {
        scrollPane.setBackground(null);
        flowPane.setBackground(null);
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(20));

        pricecombo.getItems().addAll("Ascending Price", "Descending Price", "Rating");

        weatherWebView.setVisible(false);
    }

    private void setupEventHandlers() {
        pricecombo.setOnAction(event -> sortHotels());
        destinationfiled.textProperty().addListener((observable, oldValue, newValue) -> filterHotels());
    }

    private String fetchWeatherData(String city, String country) throws Exception {
        String locationQuery = city + "," + country;
        String apiUrl = String.format(OPENWEATHER_URL, locationQuery, OPENWEATHER_API_KEY);

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP Error: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        return response.toString();
    }

    private void loadHotels() {
        hotelList = service.getHebergementsMisenAvant();
        displayedHotels = new ArrayList<>(hotelList);
        updateHotelCards(displayedHotels);
    }

    private void initializeFavoritesPanel() {
        favoritesContainer = new VBox();
        favoritesContainer.setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        favoritesContainer.setPrefWidth(900);
        favoritesContainer.setPrefHeight(510);
        favoritesContainer.setVisible(false);

        Label header = new Label("MY FAVORITES");
        header.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15; " +
                "-fx-text-fill: #495057; " +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-border-color: #e9ecef; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-background-color: #ffffff; " +
                "-fx-border-radius: 10 10 0 0;");

        favoritesListView = new ListView<>();
        favoritesListView.setCellFactory(new AccCellFavorisFactory());
        favoritesListView.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 5;");
        favoritesListView.setPrefHeight(400);
        VBox.setVgrow(favoritesListView, Priority.ALWAYS);

        favoritesContainer.setSpacing(0);
        favoritesContainer.setPadding(new Insets(0));
        favoritesContainer.getChildren().addAll(headerBox, favoritesListView);

        AnchorPane.setRightAnchor(favoritesContainer, 30.0);
        AnchorPane.setTopAnchor(favoritesContainer, 180.0);
        AnchorPane.setBottomAnchor(favoritesContainer, null);

        ((AnchorPane)scrollPane.getParent()).getChildren().add(favoritesContainer);
    }

    private void toggleFavorites() {
        if (favoritesContainer == null) {
            initializeFavoritesPanel();
        }

        favoritesVisible = !favoritesVisible;

        if (favoritesVisible) {
            List<Hebergement> favorites = hotelList.stream()
                    .filter(h -> "liked".equals(h.getFavoris()))
                    .collect(Collectors.toList());
            favoritesListView.getItems().setAll(favorites);

            favoritesContainer.setTranslateX(300);
            favoritesContainer.setVisible(true);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), favoritesContainer);
            slideIn.setToX(0);
            slideIn.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), favoritesContainer);
            slideOut.setToX(300);
            slideOut.setOnFinished(e -> favoritesContainer.setVisible(false));
            slideOut.play();
        }
    }

    private void filterHotels() {
        String keyword = destinationfiled.getText().toLowerCase();
        displayedHotels = hotelList.stream()
                .filter(hotel -> hotel.getName().toLowerCase().contains(keyword)
                        || hotel.getCountry().toLowerCase().contains(keyword)
                        || hotel.getCity().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        updateHotelCards(displayedHotels);
    }

    private void sortHotels() {
        String selected = pricecombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        switch (selected) {
            case "Ascending Price":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getPricePerNight));
                break;
            case "Descending Price":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getPricePerNight).reversed());
                break;
            case "Rating":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getRating).reversed());
                break;
        }
        updateHotelCards(displayedHotels);
    }

    private void updateHotelCards(List<Hebergement> hebergements) {
        flowPane.getChildren().clear();
        for (Hebergement hebergement : hebergements) {
            VBox card = createCard(hebergement);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createCard(Hebergement hebergement) {
        VBox carte = new VBox();
        carte.setPrefWidth(260);
        carte.setPadding(new Insets(12));
        carte.setSpacing(10);
        carte.setStyle("""
            -fx-background-color: #ffffff;
            -fx-background-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
            """);

        ImageView imageView = new ImageView();
        try {
            Image img = new Image(hebergement.getPhoto(), 240, 160, true, true);
            imageView.setImage(img);
            imageView.setFitWidth(240);
            imageView.setFitHeight(160);
            imageView.setSmooth(true);
            imageView.setPreserveRatio(false);
            imageView.setStyle("-fx-border-radius: 15; -fx-background-radius: 15;");
        } catch (Exception e) {
            System.out.println("Error loading image: " + hebergement.getPhoto());
        }

        Label labelTitre = new Label(hebergement.getName());
        labelTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        labelTitre.setTextFill(Color.web("#2e2e2e"));

        Label labelLocation = new Label("📍  " + hebergement.getCountry()+", "+hebergement.getCity());
        labelLocation.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        labelLocation.setTextFill(Color.web("#555"));

        Label labelDescription = new Label(hebergement.getDescription());
        labelDescription.setFont(Font.font("Arial", 12));
        labelDescription.setWrapText(true);
        labelDescription.setTextFill(Color.GRAY);

        Label labelPrix = new Label(String.format("%.2f TND / Night", hebergement.getPricePerNight()));
        labelPrix.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        labelPrix.setTextFill(Color.web("#009688"));

        HBox starsBox = new HBox(2);
        for (int i = 0; i < hebergement.getRating(); i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: gold; -fx-font-size: 14px;");
            starsBox.getChildren().add(star);
        }

        Image imgHeartEmpty = new Image(getClass().getResource("/imagesEya/heart_empty.png").toExternalForm());
        Image imgHeartFull = new Image(getClass().getResource("/imagesEya/heart_full.png").toExternalForm());

        ImageView heartView = new ImageView();
        heartView.setFitWidth(24);
        heartView.setFitHeight(24);

        if ("liked".equals(hebergement.getFavoris())) {
            heartView.setImage(imgHeartFull);
        } else {
            heartView.setImage(imgHeartEmpty);
        }

        Button btnFavori = new Button();
        btnFavori.setGraphic(heartView);
        btnFavori.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        btnFavori.setVisible(false);

        btnFavori.setOnAction(e -> {
            try {
                String newFavorisStatus;
                if ("liked".equals(hebergement.getFavoris())) {
                    newFavorisStatus = "unliked";
                    heartView.setImage(imgHeartEmpty);
                } else {
                    newFavorisStatus = "liked";
                    heartView.setImage(imgHeartFull);
                }

                service.updateFavoris(hebergement.getId(), newFavorisStatus);
                hebergement.setFavoris(newFavorisStatus);
            } catch (SQLException ex) {
                System.err.println("Error updating favorite status: " + ex.getMessage());
                if ("liked".equals(hebergement.getFavoris())) {
                    heartView.setImage(imgHeartFull);
                } else {
                    heartView.setImage(imgHeartEmpty);
                }
            }
        });

        Button btnReserver = new Button("Book");
        btnReserver.setStyle("""
            -fx-background-color: #588b8b;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-padding: 6 12;
            -fx-font-weight: bold;
            """);
        btnReserver.setVisible(false);

        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.setSpacing(10);

        footerBox.getChildren().add(btnFavori);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footerBox.getChildren().add(spacer);
        footerBox.getChildren().add(btnReserver);

        VBox weatherBox = new VBox();
        weatherBox.setStyle("""
            -fx-background-color: #f8f9fa;
            -fx-background-radius: 10;
            -fx-padding: 8;
            -fx-spacing: 5;
            """);
        weatherBox.setVisible(false);

        Label weatherLoading = new Label("Loading weather...");
        weatherLoading.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        weatherBox.getChildren().add(weatherLoading);

        carte.setOnMouseEntered(e -> {
            btnReserver.setVisible(true);
            btnFavori.setVisible(true);
            weatherBox.setVisible(true);

            new Thread(() -> {
                try {
                    String weatherData = fetchWeatherData(hebergement.getCity(), hebergement.getCountry());
                    JSONObject json = new JSONObject(weatherData);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject weather = json.getJSONArray("weather").getJSONObject(0);

                    double temp = main.getDouble("temp");
                    String description = weather.getString("description");
                    String iconCode = weather.getString("icon");

                    Platform.runLater(() -> {
                        weatherBox.getChildren().clear();

                        HBox weatherHeader = new HBox(5);
                        weatherHeader.setAlignment(Pos.CENTER_LEFT);

                        ImageView weatherIcon = new ImageView();
                        weatherIcon.setFitWidth(30);
                        weatherIcon.setFitHeight(30);
                        weatherIcon.setImage(new Image("https://openweathermap.org/img/wn/" + iconCode + "@2x.png"));

                        Label weatherTemp = new Label(String.format("%.1f°C", temp));
                        weatherTemp.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                        Label weatherDesc = new Label(description.substring(0, 1).toUpperCase() + description.substring(1));
                        weatherDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                        weatherHeader.getChildren().addAll(weatherIcon, weatherTemp, weatherDesc);

                        HBox weatherDetails = new HBox(10);
                        weatherDetails.setAlignment(Pos.CENTER);

                        Label feelsLike = new Label(String.format("Feels: %.1f°C", main.getDouble("feels_like")));
                        feelsLike.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                        Label humidity = new Label(String.format("Humidity: %d%%", main.getInt("humidity")));
                        humidity.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

                        weatherDetails.getChildren().addAll(feelsLike, humidity);
                        weatherBox.getChildren().addAll(weatherHeader, weatherDetails);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        weatherBox.getChildren().clear();
                        Label errorLabel = new Label("Weather data unavailable");
                        errorLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 12px;");
                        weatherBox.getChildren().add(errorLabel);
                    });
                }
            }).start();

            carte.setStyle("""
                -fx-background-color: #f5f5f5;
                -fx-background-radius: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);
                """);
        });

        carte.setOnMouseExited(e -> {
            btnReserver.setVisible(false);
            btnFavori.setVisible(false);
            weatherBox.setVisible(false);
            carte.setStyle("""
                -fx-background-color: #ffffff;
                -fx-background-radius: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
                """);
        });

        btnReserver.setOnAction(event -> openReservation(hebergement, event));

        carte.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openHotelInfo(hebergement);
            }
        });

        carte.getChildren().addAll(
                imageView,
                labelTitre,
                labelLocation,
                labelDescription,
                labelPrix,
                starsBox,
                weatherBox,
                footerBox
        );

        return carte;
    }

    private void openHotelInfo(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlEya/hotel_info_client.fxml"));
            Node node = loader.load();

            HotelInfoClient hotelInfoController = loader.getController();
            hotelInfoController.setHebergementDetails(hebergement);

            if (mainController != null) {
                hotelInfoController.setMainController(mainController);
                mainController.loadFXML(String.valueOf(node));
            } else {
                System.err.println("MainController is not set in ClientAcc");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load hotel info");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void openReservation(Hebergement hebergement, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlEya/reservationclient.fxml"));
            Node node = loader.load();

            ReservationClient controller = loader.getController();
            controller.setHebergementData(hebergement);

            if (controller instanceof ControlledScreen && mainController != null) {
                ((ControlledScreen) controller).setMainController(mainController);
            }

            if (mainController != null) {
                mainController.loadFXML(String.valueOf(node));
            } else {
                System.err.println("MainController is not set in ClientAcc");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load reservation page");
            alert.setContentText("An error occurred: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void initializeBookedPanel() {
        bookedContainer = new VBox();
        bookedContainer.setStyle("-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #dee2e6; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        bookedContainer.setPrefWidth(900);
        bookedContainer.setPrefHeight(510);
        bookedContainer.setVisible(false);

        Label header = new Label("MY BOOKINGS"); // Declare and initialize header
        header.setStyle("-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 15; " +
                "-fx-text-fill: #495057; " +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-border-color: #e9ecef; " +
                "-fx-border-width: 0 0 1 0; " +
                "-fx-background-color: #ffffff; " +
                "-fx-border-radius: 10 10 0 0;");

        bookedListView = new ListView<>();
        bookedListView.setCellFactory(new AccCellBookedFactory(this::refreshBookedList));
        bookedListView.setStyle("-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 5;");
        bookedListView.setPrefHeight(400);
        VBox.setVgrow(bookedListView, Priority.ALWAYS);

        bookedContainer.setSpacing(0);
        bookedContainer.setPadding(new Insets(0));
        bookedContainer.getChildren().addAll(headerBox, bookedListView);

        AnchorPane.setRightAnchor(bookedContainer, 30.0);
        AnchorPane.setTopAnchor(bookedContainer, 180.0);
        AnchorPane.setBottomAnchor(bookedContainer, null);

        ((AnchorPane)scrollPane.getParent()).getChildren().add(bookedContainer);
    }

    private void toggleBooked() {
        if (bookedContainer == null) {
            initializeBookedPanel();
        }

        bookedVisible = !bookedVisible;

        if (bookedVisible) {
            refreshBookedList();

            bookedContainer.setTranslateX(300);
            bookedContainer.setVisible(true);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), bookedContainer);
            slideIn.setToX(0);
            slideIn.play();
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), bookedContainer);
            slideOut.setToX(300);
            slideOut.setOnFinished(e -> bookedContainer.setVisible(false));
            slideOut.play();
        }
    }

    private void refreshBookedList() {
        try {
            ServiceReservation service = new ServiceReservation();
            List<Reservation> bookings = service.getReservationsByUserId(1);
            bookedListView.getItems().setAll(bookings);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load bookings");
            alert.setContentText("An error occurred while trying to load your bookings.");
            alert.showAndWait();
        }
    }
}