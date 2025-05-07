package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

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
    private Button btnBooking;

    private Timeline hoverCycle;
    private boolean showingText = false;
    private Timeline prHoverCycle;
    private Timeline boHoverCycle;
    private Timeline stHoverCycle;
    private Timeline loHoverCycle;
    private boolean prShowingText = false;
    private boolean boShowingText = false;
    private boolean stShowingText = false;
    private boolean loShowingText = false;


    private ContextMenu bookingMenu;

    @FXML
    private void initialize() {
        bookingMenu = new ContextMenu();

        MenuItem transportItem = new MenuItem("Transportation");
        transportItem.setOnAction(this::handleTransport);

        MenuItem accommodationItem = new MenuItem("Accommodation");
        accommodationItem.setOnAction(this::handleAccommodation);

        MenuItem flightItem = new MenuItem("Flight");
        flightItem.setOnAction(this::handleFlight);

        bookingMenu.getItems().addAll(transportItem, accommodationItem, flightItem);
    }

    @FXML
    private void showBookingMenu() {
        if (!bookingMenu.isShowing()) {
            // Show it to the right of the button
            bookingMenu.show(btnBooking, Side.RIGHT, 10, 0);
        } else {
            bookingMenu.hide();
        }
    }


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

    //88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888



    public void handlSettings(ActionEvent actionEvent) {
    }

    public void handlLogout(ActionEvent actionEvent) {
    }

    public void handleHome(ActionEvent actionEvent) {
    }

    public void handlProgram(ActionEvent actionEvent) {
    }

    private void handleFlight(ActionEvent actionEvent) {
    }

    private void handleAccommodation(ActionEvent actionEvent) {
        try {
            // Clear existing content
            contentPane.getChildren().clear();

            // Load new FXML content
            Node newContent = FxmlUtils.loadFXML("/fxmlEya/client_acc.fxml");
            contentPane.getChildren().add(newContent);


        } catch (IOException e) {
            e.printStackTrace();
            // Show error to user
        }
    }

    @FXML
    private Pane contentPane;
    public void handleTransport(ActionEvent mouseEvent) {
        try {
            // Clear existing content
            contentPane.getChildren().clear();

            // Load new FXML content
            Node newContent = FxmlUtils.loadFXML("/fxml/TransportBook.fxml");
            contentPane.getChildren().add(newContent);


        } catch (IOException e) {
            e.printStackTrace();
            // Show error to user
        }
    }
}
