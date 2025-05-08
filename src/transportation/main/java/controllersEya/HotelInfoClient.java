package controllersEya;

import controllers.MenuController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelsEya.Hebergement;
import services.ControlledScreen;

import java.io.File;

public class HotelInfoClient implements ControlledScreen {
    @FXML private Label nameinfo;
    @FXML private Label typeinfo;
    @FXML private Label cityinfo;
    @FXML private Label countryinfo;
    @FXML private Label addressinfo;
    @FXML private Label descriptioninfo;
    @FXML private Label capacityinfo;
    @FXML private Label ratinginfo;
    @FXML private Label dispoinfo;
    @FXML private Label priceinf;
    @FXML private Label priceinfo;
    @FXML private Label optionsinfo;
    @FXML private ImageView photoinfo;
    @FXML private HBox albuminfoo;
    @FXML private Button returnButton;

    private MenuController mainController;

    @Override
    public void setMainController(MenuController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        returnButton.setOnAction(event -> retournerClient());
    }

    public void retournerClient() {
        if (mainController != null) {
            mainController.loadFXML("/fxmlEya/client_acc.fxml");
        } else {
            System.err.println("MainController is not set in HotelInfoClient");
        }
    }

    public void setHebergementDetails(Hebergement h) {
        nameinfo.setText(h.getName());
        typeinfo.setText(h.getType());
        cityinfo.setText(h.getCity());
        countryinfo.setText(h.getCountry());
        addressinfo.setText(h.getAddress());
        descriptioninfo.setText(h.getDescription());
        capacityinfo.setText(String.valueOf(h.getCapacity()));
        ratinginfo.setText(getStarRating(h.getRating()));
        dispoinfo.setText(h.isDisponibility() ? "Available" : "Unavailable");
        priceinfo.setText(String.format("%.2f TND", h.getPricePerNight()));
        optionsinfo.setText(h.getOptions());
        String photoPath = h.getPhoto();

        try {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                photoinfo.setImage(image);
            } else {
                System.out.println("Fichier image introuvable : " + photoPath);
                photoinfo.setImage(new Image(getClass().getResourceAsStream("/images/default.jpg")));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
        System.out.println("Chemin reçu de la base : " + h.getPhoto());
        System.out.println("Fichier existe ? " + new File(h.getPhoto()).exists());

        // Album
        albuminfoo.getChildren().clear();
        if (h.getAlbum() != null && !h.getAlbum().isEmpty()) {
            String[] imagePaths = h.getAlbum().split("\n");
            for (String path : imagePaths) {
                File file = new File(path.trim());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(160);
                    imageView.setFitHeight(160);
                    imageView.setPreserveRatio(true);
                    imageView.getStyleClass().add("image-thumbnail");

                    imageView.setOnMouseClicked(event -> {
                        Stage stage = new Stage();
                        stage.setTitle("Aperçu de l'image");
                        ImageView fullSize = new ImageView(image);
                        fullSize.setPreserveRatio(true);
                        fullSize.setFitWidth(600);
                        StackPane root = new StackPane(fullSize);
                        root.setPadding(new Insets(10));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();
                    });

                    albuminfoo.getChildren().add(imageView);
                } else {
                    System.out.println("Image non trouvée : " + path);
                }
            }
        } else {
            System.out.println("Aucune image dans l’album.");
        }
    }

    private String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;
        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (halfStar) {
            stars.append("☆");
        }
        while (stars.length() < 5) {
            stars.append("☆");
        }
        return stars.toString();
    }
}