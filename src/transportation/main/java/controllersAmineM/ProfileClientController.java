package controllersAmineM;


import entitiesAmineM.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import servicesAmineM.ServiceUser;
import servicesAmineM.Session;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.io.*;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ProfileClientController implements SigninController.UserAwareController {

    @FXML private ImageView profilePhotoView;
    @FXML private Button btnUploadPhoto;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField passportField;
    @FXML private Label nameErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label passportErrorLabel;

    private User user;
    private ServiceUser serviceUser;
    private static final String DEFAULT_PROFILE_IMAGE = "/imagesAmineM/user-icon7.png";
    private static final String PROFILE_PHOTOS_DIR = "/imagesAmineM/";

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");

    private static final Logger LOGGER = Logger.getLogger(ProfileClientController.class.getName());

    @Override
    public void setUser(User user) {
        this.user = user;
        if (serviceUser == null) {
            try {
                this.serviceUser = new ServiceUser();
                LOGGER.info("ServiceUser initialized in setUser");
            } catch (Exception e) {
                LOGGER.severe("Failed to initialize ServiceUser in setUser: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (user != null) {
            loadProfile();
        } else {
            LOGGER.warning("setUser called with null user");
        }
    }

    private void loadProfile() {
        try {
            // Refresh user data
            user = serviceUser.getUserById(user.getId());
            if (user != null) {
                // Update Session
                Session.setCurrentUser(Session.getSession(user));
                LOGGER.info("Session updated with refreshed user data for ID: " + user.getId());

                // Update UI fields
                nameField.setText(user.getName());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
                addressField.setText(user.getAddress() != null ? user.getAddress() : "");
                passportField.setText(user.getPassport() != null ? user.getPassport() : "");

                // Load profile photo
                String photoPath = user.getProfilePhotoPath();
                if (photoPath != null && !photoPath.isEmpty()) {
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        profilePhotoView.setImage(new Image(photoFile.toURI().toString()));
                        LOGGER.info("Profile photo loaded for user ID: " + user.getId() + " from path: " + photoPath);
                    } else {
                        LOGGER.warning("Profile photo file not found: " + photoPath);
                        profilePhotoView.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));
                    }
                } else {
                    LOGGER.info("No profile photo path for user ID: " + user.getId() + ", using default image");
                    profilePhotoView.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));
                }
            } else {
                LOGGER.severe("Failed to load user data for ID: " + user.getId());
                showAlert("Error", "User not found in database.");
            }
        } catch (SQLException e) {
            LOGGER.severe("SQLException loading profile for user ID: " + user.getId() + ", error: " + e.getMessage());
            showAlert("Database Error", "Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadProfilePhoto() throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(btnUploadPhoto.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create profile_photos directory if it doesn't exist
                File photosDir = new File(PROFILE_PHOTOS_DIR);
                if (!photosDir.exists()) {
                    photosDir.mkdirs();
                    LOGGER.info("Created profile photos directory: " + PROFILE_PHOTOS_DIR);
                }

                // Generate a unique filename
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String uniqueFileName = UUID.randomUUID().toString() + extension;
                File destFile = new File(PROFILE_PHOTOS_DIR + uniqueFileName);

                // Copy the file to the profile_photos directory
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Copied uploaded file to: " + destFile.getAbsolutePath());

                // Update the database with the relative path
                String relativePath = PROFILE_PHOTOS_DIR + uniqueFileName;
                serviceUser.updateProfilePhoto(user.getId(), relativePath);

                // Update profilePhotoView
                profilePhotoView.setImage(new Image(destFile.toURI().toString()));

                // Update the User object in the session
                User updatedUser = serviceUser.getUserById(user.getId());
                Session.setCurrentUser(Session.getSession(updatedUser));
                LOGGER.info("Session updated with new profile photo path for user ID: " + user.getId());

                showAlert("Success", "Profile photo updated successfully.");
            } catch (IOException e) {
                LOGGER.severe("IOException uploading profile photo: " + e.getMessage());
                showAlert("Error", "Failed to save the selected file: " + e.getMessage());
            } catch (SQLException e) {
                LOGGER.severe("SQLException uploading profile photo: " + e.getMessage());
                showAlert("Error", "Database error while uploading profile photo: " + e.getMessage());
            }
        } else {
            LOGGER.info("No file selected for profile photo upload");
        }
    }

    @FXML
    private void removeProfilePhoto() {
        try {
            // Update database to set profile_photo_path to NULL
            serviceUser.updateProfilePhoto(user.getId(), null);

            // Update profilePhotoView with default image
            profilePhotoView.setImage(new Image(getClass().getResource(DEFAULT_PROFILE_IMAGE).toExternalForm()));

            // Update the User object in the session
            User updatedUser = serviceUser.getUserById(user.getId());
            Session.setCurrentUser(Session.getSession(updatedUser));
            LOGGER.info("Session updated with removed profile photo for user ID: " + user.getId());

            showAlert("Success", "Profile photo removed successfully.");
        } catch (SQLException e) {
            LOGGER.severe("SQLException removing profile photo: " + e.getMessage());
            showAlert("Error", "Database error while removing profile photo: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.severe("IOException removing profile photo: " + e.getMessage());
            showAlert("Error", "Unexpected I/O error: " + e.getMessage());
        }
    }

    @FXML
    private void saveProfile() throws SQLException {
        boolean valid = true;
        nameErrorLabel.setText("");
        phoneErrorLabel.setText("");
        passportErrorLabel.setText("");

        // Validate Name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            nameErrorLabel.setText("Name is required.");
            valid = false;
        }

        // Validate Phone
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            phoneErrorLabel.setText("Invalid phone number (e.g., +1234567890).");
            valid = false;
        }

        // Validate Passport
        String passport = passportField.getText().trim();
        if (!passport.isEmpty() && !PASSPORT_PATTERN.matcher(passport).matches()) {
            passportErrorLabel.setText("Invalid passport (6-12 alphanumeric).");
            valid = false;
        }

        if (valid) {
            // Update user data
            user.setName(name);
            user.setPhone(phone.isEmpty() ? null : phone);
            user.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
            user.setPassport(passport.isEmpty() ? null : passport);
            serviceUser.update(user);
            showAlert("Success", "Profile updated successfully.");
            loadProfile();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}