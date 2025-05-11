package controllersAmineM;


/*import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.*;
import java.sql.Types;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static com.example.airPlan.Utiles.DBConnection2.getConnection;
*/
public class ClientController2 /*implements Initializable*/ {

    /*@FXML private BorderPane root;
    @FXML private Label clientNameLabel;
    @FXML private Button btnDashboard;
    @FXML private Button btnLogout;
    @FXML private ImageView profilePhotoView;
    @FXML private Button btnUploadPhoto;
    @FXML private Button btnRemovePhoto;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField passportField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label nameErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label passportErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Button btnSave;
    @FXML private Button btnDeleteAccount;
    @FXML private Button btnSettings;
    @FXML private VBox passwordResetSection;

    private User user;
    private ServiceUser serviceUser;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{10,15}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceUser = new ServiceUser();
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        user = Session.getCurrentUser();
        System.out.println("ClientController2: User from session: " + (user != null ? user.getName() : "null"));
        if (user != null && "CLIENT".equals(user.getUserType())) {
            loadProfile();
        } else {
            showAlert("Session Error", "Invalid user session. Please sign in as client.");
            redirectToSignin();
        }
    }

    private void loadProfile() {
        try {
            // Refresh user data
            user = serviceUser.getUserById(user.getId());
            clientNameLabel.setText("Client: " + user.getName());
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            addressField.setText(user.getAddress() != null ? user.getAddress() : "");
            passportField.setText(user.getPassport() != null ? user.getPassport() : "");

            // Load profile photo
            byte[] photoData = user.getProfilePhoto();
            if (photoData != null && photoData.length > 0) {
                Image image = new Image(new ByteArrayInputStream(photoData));
                profilePhotoView.setImage(image);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void uploadProfilePhoto() throws SQLException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(btnUploadPhoto.getScene().getWindow());
        if (selectedFile != null) {
            serviceUser.updateProfilePhoto(user.getId(), selectedFile);
            byte[] photoData = serviceUser.getProfilePhoto(user.getId());
            if (photoData != null) {
                Image image = new Image(new ByteArrayInputStream(photoData));
                profilePhotoView.setImage(image);
            }
            showAlert("Success", "Profile photo updated successfully.");
        }
    }

    @FXML
    private void removeProfilePhoto() throws SQLException, IOException {
        serviceUser.updateProfilePhoto(user.getId(), null);
        profilePhotoView.setImage(new Image(getClass().getResource("/images/PNG_icons/user-icon7.png").toExternalForm()));
        showAlert("Success", "Profile photo removed successfully.");
    }

    @FXML
    private void saveProfile() {
        try {
            boolean valid = true;
            nameErrorLabel.setText("");
            phoneErrorLabel.setText("");
            passportErrorLabel.setText("");
            passwordErrorLabel.setText("");

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

            // Validate Password (if visible)
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            if (passwordResetSection.isVisible() && (!newPassword.isEmpty() || !confirmPassword.isEmpty())) {
                if (!newPassword.equals(confirmPassword)) {
                    passwordErrorLabel.setText("Passwords do not match.");
                    valid = false;
                } else if (newPassword.length() < 8) {
                    passwordErrorLabel.setText("Password must be at least 8 characters.");
                    valid = false;
                }
            }

            if (valid) {
                // Update user data
                user.setName(name);
                user.setPhone(phone.isEmpty() ? null : phone);
                user.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                user.setPassport(passport.isEmpty() ? null : passport);
                if (passwordResetSection.isVisible() && !newPassword.isEmpty()) {
                    user.setPassword(serviceUser.hashPassword(newPassword));
                }
                serviceUser.update(user);
                showAlert("Success", "Profile updated successfully.");
                loadProfile();
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to save profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Account");
        confirm.setHeaderText("Are you sure you want to delete your account?");
        confirm.setContentText("This action cannot be undone.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                serviceUser.delete(user.getId());
                Session.clearSession();
                redirectToSignin();
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete account: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void goToDashboard() {
        try {
            if (user == null || !"CLIENT".equals(user.getUserType())) {
                showAlert("Session Error", "Invalid user session. Please sign in as client.");
                redirectToSignin();
                return;
            }
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Client/ClientDashboard.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
        try {
            Session.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/signin.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to log out: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void togglePasswordReset() {
        boolean isVisible = passwordResetSection.isVisible();
        passwordResetSection.setVisible(!isVisible);
        passwordResetSection.setManaged(!isVisible);
        btnSettings.setText(isVisible ? "Settings" : "Hide Password Reset");
    }

    private void redirectToSignin() {
        try {
            Session.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/signin.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to redirect to sign-in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setUser(User user) throws SQLException {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users SET name = ?, phone = ?, address = ?, passport = ?, password = ?, profile_photo = ? WHERE user_id = ?"
            );
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getAddress());
            stmt.setString(4, user.getPassport());
            stmt.setString(5, user.getPassword());
            if (user.getProfilePhoto() != null) {
                stmt.setBinaryStream(6, new ByteArrayInputStream(user.getProfilePhoto()), user.getProfilePhoto().length);
            } else {
                stmt.setNull(6, Types.BLOB);
            }
            stmt.setInt(7, user.getId());
            stmt.executeUpdate();
        }
    }*/
}