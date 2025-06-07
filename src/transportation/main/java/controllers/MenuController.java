package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import utils.FxmlUtils;

import java.io.IOException;

public class MenuController {
    public Circle notificationIndicator;
    @FXML
    private Button btnHome;
    @FXML
    private StackPane homeGraphic;
    @FXML
    private Label homeLabel;
    @FXML
    private FontAwesomeIconView homeIcon;
    @FXML
    private Label prLabel;
    @FXML
    private FontAwesomeIconView prIcon;
    @FXML
    private Label boLabel;
    @FXML
    private FontAwesomeIconView boIcon;
    @FXML
    private Label stLabel;
    @FXML
    private FontAwesomeIconView stIcon;
    @FXML
    private Label loLabel;
    @FXML
    private FontAwesomeIconView loIcon;
    @FXML
    private Label fLabel;
    @FXML
    private FontAwesomeIconView fIcon;
    @FXML
    private Button btnFlight;
    @FXML
    private Label aLabel;
    @FXML
    private FontAwesomeIconView aIcon;
    @FXML
    private Button btnAccommodation;
    @FXML
    private Label tLabel;
    @FXML
    private FontAwesomeIconView tIcon;
    @FXML
    private Button btnTransport;
    @FXML
    private Label cLabel;
    @FXML
    private ImageView cpic;
    @FXML
    private Button btnChat;


    //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

    public void startHomeHover(MouseEvent mouseEvent) {
        // Fade out icon while fading in label
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), homeIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), homeLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            homeIcon.setVisible(false);
            homeLabel.setVisible(true);
            fadeInLabel.play();
        });

        // Start transitions
        homeLabel.setOpacity(0);
        homeLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopHomeHover(MouseEvent mouseEvent) {
        // Fade out label while fading in icon
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), homeLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), homeIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            homeLabel.setVisible(false);
            homeIcon.setVisible(true);
            fadeInIcon.play();
        });

        // Start transitions
        homeIcon.setOpacity(0);
        homeIcon.setVisible(true);
        fadeOutLabel.play();
    }


    // Program Button Hover
    public void startPrHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), prIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), prLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            prIcon.setVisible(false);
            prLabel.setVisible(true);
            fadeInLabel.play();
        });

        prLabel.setOpacity(0);
        prLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopPrHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), prLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), prIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            prLabel.setVisible(false);
            prIcon.setVisible(true);
            fadeInIcon.play();
        });

        prIcon.setOpacity(0);
        prIcon.setVisible(true);
        fadeOutLabel.play();
    }

    // Booking Button Hover
    public void startBoHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), boIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), boLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            boIcon.setVisible(false);
            boLabel.setVisible(true);
            fadeInLabel.play();
        });

        boLabel.setOpacity(0);
        boLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopBoHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), boLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), boIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            boLabel.setVisible(false);
            boIcon.setVisible(true);
            fadeInIcon.play();
        });

        boIcon.setOpacity(0);
        boIcon.setVisible(true);
        fadeOutLabel.play();
    }

    // Settings Button Hover
    public void startStHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), stIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), stLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            stIcon.setVisible(false);
            stLabel.setVisible(true);
            fadeInLabel.play();
        });

        stLabel.setOpacity(0);
        stLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopStHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), stLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), stIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            stLabel.setVisible(false);
            stIcon.setVisible(true);
            fadeInIcon.play();
        });

        stIcon.setOpacity(0);
        stIcon.setVisible(true);
        fadeOutLabel.play();
    }

    // Logout Button Hover
    public void startLoHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), loIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), loLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            loIcon.setVisible(false);
            loLabel.setVisible(true);
            fadeInLabel.play();
        });

        loLabel.setOpacity(0);
        loLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopLoHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), loLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), loIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            loLabel.setVisible(false);
            loIcon.setVisible(true);
            fadeInIcon.play();
        });

        loIcon.setOpacity(0);
        loIcon.setVisible(true);
        fadeOutLabel.play();
    }

    public void startfHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), fIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), fLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            fIcon.setVisible(false);
            fLabel.setVisible(true);
            fadeInLabel.play();
        });

        fLabel.setOpacity(0);
        fLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopfHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), fLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), fIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            fLabel.setVisible(false);
            fIcon.setVisible(true);
            fadeInIcon.play();
        });

        fIcon.setOpacity(0);
        fIcon.setVisible(true);
        fadeOutLabel.play();
    }

    public void startAHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), aIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), aLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            aIcon.setVisible(false);
            aLabel.setVisible(true);
            fadeInLabel.play();
        });

        aLabel.setOpacity(0);
        aLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopAHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), aLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), aIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            aLabel.setVisible(false);
            aIcon.setVisible(true);
            fadeInIcon.play();
        });

        aIcon.setOpacity(0);
        aIcon.setVisible(true);
        fadeOutLabel.play();
    }

    public void starttHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), tIcon);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), tLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            tIcon.setVisible(false);
            tLabel.setVisible(true);
            fadeInLabel.play();
        });

        tLabel.setOpacity(0);
        tLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stoptHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), tLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), tIcon);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            tLabel.setVisible(false);
            tIcon.setVisible(true);
            fadeInIcon.play();
        });

        tIcon.setOpacity(0);
        tIcon.setVisible(true);
        fadeOutLabel.play();
    }

    public void startcHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutIcon = new FadeTransition(Duration.millis(300), cpic);
        fadeOutIcon.setFromValue(1.0);
        fadeOutIcon.setToValue(0.0);

        FadeTransition fadeInLabel = new FadeTransition(Duration.millis(300), cLabel);
        fadeInLabel.setFromValue(0.0);
        fadeInLabel.setToValue(1.0);

        fadeOutIcon.setOnFinished(e -> {
            cpic.setVisible(false);
            cLabel.setVisible(true);
            fadeInLabel.play();
        });

        cLabel.setOpacity(0);
        cLabel.setVisible(true);
        fadeOutIcon.play();
    }

    public void stopcHover(MouseEvent mouseEvent) {
        FadeTransition fadeOutLabel = new FadeTransition(Duration.millis(300), cLabel);
        fadeOutLabel.setFromValue(1.0);
        fadeOutLabel.setToValue(0.0);

        FadeTransition fadeInIcon = new FadeTransition(Duration.millis(300), cpic);
        fadeInIcon.setFromValue(0.0);
        fadeInIcon.setToValue(1.0);

        fadeOutLabel.setOnFinished(e -> {
            cLabel.setVisible(false);
            cpic.setVisible(true);
            fadeInIcon.play();
        });

        cpic.setOpacity(0);
        cpic.setVisible(true);
        fadeOutLabel.play();
    }

    //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888



    public void handlSettings(ActionEvent actionEvent) {
    }

    public void handlLogout(ActionEvent actionEvent) {
    }

    public void handleHome(ActionEvent actionEvent) {
    }

    public void handlProgram(ActionEvent actionEvent) {
    }

    @FXML
    private void handleFlight(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAccommodation(ActionEvent actionEvent) {
    }

    @FXML
    private Pane contentPane;
    public void handleTransport(ActionEvent mouseEvent) {
        try {
            // Clear existing content
            contentPane.getChildren().clear();

            // Load new FXML content
            Node newContent = FxmlUtils.loadFXML("/fxml/TransportBookAdmin.fxml");
            contentPane.getChildren().add(newContent);


        } catch (IOException e) {
            e.printStackTrace();
            // Show error to user
        }
    }

    public void handleChat(ActionEvent actionEvent) {
    }

}