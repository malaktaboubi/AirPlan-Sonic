package controllers;

import controllersAmineM.ApplicationContext;
import controllersAmineM.ProfileClientController;
import controllersAmineM.SigninController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entitiesAmineM.User;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import servicesAmineM.ServiceUser;
import servicesAmineM.Session;
import utils.FxmlUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class MenuController implements SigninController.UserAwareController {
    @FXML
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
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userTypeLabel;
    @FXML
    private ImageView profileImage;

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());
    private User currentUser;
    private static final String PROFILE_PHOTOS_DIR = "/imagesAmineM/";
    private static final String DEFAULT_PROFILE_IMAGE = "/imagesAmineM/user-icon7.png";
    @FXML
    private Button btnSettings;
    @FXML
    private FontAwesomeIconView notificationIcon;
    @FXML
    private Pane contentPane;
    @FXML
    private StackPane PlanGraphic;
    @FXML
    private Button btnProgram;
    @FXML
    private Button btnLogout;
    @FXML
    private StackPane rootPane;
    @FXML
    private Button notificationButton;
    @FXML
    private Pane TopBar;
    @FXML
    private Button profileButton;

    private ServiceUser serviceUser;

    public MenuController() {
        try {
            this.serviceUser = new ServiceUser();
            LOGGER.info("ServiceUser initialized in MenuController constructor");
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize ServiceUser: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setUser(User user) {
        this.currentUser = user;
        updateProfileUI();
    }

    public void initialize() {
        rootPane.setUserData(this); // Assuming rootPane is the top-level Pane
        updateProfileUI();
    }

    public void refreshProfileUI() {
        updateProfileUI();
        LOGGER.info("Profile UI refreshed");
    }

    private void updateProfileUI() {
        User sessionUser = Session.getCurrentUser() != null ? Session.getCurrentUser() : currentUser;

        if (sessionUser == null || serviceUser == null) {
            LOGGER.warning("Cannot update profile UI: sessionUser or serviceUser is null");
            setDefaultUI();
            return;
        }

        try {
            LOGGER.info("Fetching user data for ID: " + sessionUser.getId());
            User refreshedUser = serviceUser.getUserById(sessionUser.getId());
            if (refreshedUser != null) {
                sessionUser = refreshedUser;
                // Update Session
                Session.setCurrentUser(Session.getSession(sessionUser));
                LOGGER.info("Session updated with refreshed user data for ID: " + sessionUser.getId());

                // Set username
                userNameLabel.setText(sessionUser.getName());
                LOGGER.info("Username set to: " + sessionUser.getName());

                // Set user type
                userTypeLabel.setText(sessionUser.getUserType());
                LOGGER.info("User type set to: " + sessionUser.getUserType());

                // Set profile image
                String photoPath = serviceUser.getProfilePhotoPath(sessionUser.getId());
                if (photoPath != null && !photoPath.isEmpty()) {
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        profileImage.setImage(new Image(photoFile.toURI().toString()));
                        LOGGER.info("Profile photo loaded for user ID: " + sessionUser.getId() + " from path: " + photoPath);
                    } else {
                        LOGGER.warning("Profile photo file not found: " + photoPath);
                        profileImage.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));
                    }
                } else {
                    LOGGER.info("No profile photo path for user ID: " + sessionUser.getId() + ", using default image");
                    profileImage.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));
                }

                // Apply circular clip to profile image
                Circle clip = new Circle(20, 20, 20);
                profileImage.setClip(clip);
            } else {
                LOGGER.warning("User not found in database for ID: " + sessionUser.getId());
                setDefaultUI();
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLException fetching user data for ID: " + sessionUser.getId() + ", error: " + e.getMessage());
            e.printStackTrace();
            setDefaultUI();
        }
    }

    private void setDefaultUI() {
        userNameLabel.setText("Guest");
        userTypeLabel.setText("Client");
        profileImage.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));
        Circle clip = new Circle(20, 20, 20);
        profileImage.setClip(clip);
        LOGGER.info("Default UI set: Guest/Client with default profile image");
    }

    //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

    @FXML
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

    @FXML
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

    @FXML
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

    @FXML
    public void stopPrHover(MouseEvent event) {
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

    @FXML
    public void startfHover(MouseEvent event) {
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

    @FXML
    public void stopfHover(MouseEvent event) {
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

    @FXML
    public void startAHover(MouseEvent event) {
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

    @FXML
    public void stopAHover(MouseEvent event) {
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

    @FXML
    public void starttHover(MouseEvent event) {
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

    @FXML
    public void stoptHover(MouseEvent event) {
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

    @FXML
    public void startStHover(MouseEvent event) {
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

    @FXML
    public void stopStHover(MouseEvent event) {
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

    @FXML
    public void startLoHover(MouseEvent event) {
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

    @FXML
    public void stopLoHover(MouseEvent event) {
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
    //888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888


    @FXML
    public void handleHome(ActionEvent event) {
        // Clear existing content
        contentPane.getChildren().clear();
    }

    @FXML
    public void handlProgram(ActionEvent event) {
    }

    @FXML
    public void handleFlight(ActionEvent actionEvent) {
    }

    @FXML
    public void handleAccommodation(ActionEvent actionEvent) {
    }

    @FXML
    public void handleTransport(ActionEvent actionEvent) {
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

    @FXML
    public void handlSettings(ActionEvent actionEvent) {
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handlLogout(ActionEvent actionEvent) {
        try {
            Session.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/fxmlAmineM/signin.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to log out: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleProfileButton(ActionEvent actionEvent) {
        try {
            User sessionUser = Session.getCurrentUser();
            if (sessionUser == null) {
                LOGGER.warning("Session expired or user not logged in. Redirecting to signin page.");
                showAlert("Session Expired", "Please sign in again.");
                Parent root = FXMLLoader.load(getClass().getResource("/fxmlAmineM/signin.fxml"));
                Stage stage = (Stage) profileButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
                return;
            }

            contentPane.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxmlAmineM/ProfileClient.fxml"));
            Node newContent = loader.load();

            ProfileClientController controller = loader.getController();
            controller.setUser(sessionUser);
            controller.setMenuController(this); // Pass MenuController instance
            LOGGER.info("Set user and MenuController in ProfileClientController for ID: " + sessionUser.getId());

            contentPane.getChildren().add(newContent);
        } catch (IOException e) {
            LOGGER.severe("Failed to load ProfileClient.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load profile page: " + e.getMessage());
        }
    }
}
