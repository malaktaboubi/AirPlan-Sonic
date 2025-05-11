package controllersAmineM;

import entitiesAmineM.User;
import servicesAmineM.ServiceUser;
import servicesAmineM.Session;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgencyController2 implements Initializable, UserAwareController {

    @FXML private BorderPane root;
    @FXML private Label agencyNameLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label nameErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Button btnSave;
    @FXML private Button btnLogout;
    @FXML private Button btnDeleteAccount;

    private User user;
    private ServiceUser serviceUser;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox profileView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceUser = new ServiceUser();
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        // Check session for user if not set
        if (user == null) {
            user = Session.getCurrentUser();
            System.out.println("AgencyController: User from session: " + (user != null ? user.getName() : "null"));
            if (user != null) {
                loadProfile();
            }
        }
    }

    @Override
    public void setUser(User user) {
        this.user = user;
        System.out.println("AgencyController: setUser called with: " + (user != null ? user.getName() : "null"));
        loadProfile();
    }

    private void loadProfile() {
        if (user != null) {
            agencyNameLabel.setText("Agency: " + user.getName());
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            addressField.setText(user.getAddress() != null ? user.getAddress() : "");
        }
    }

    @FXML
    private void saveProfile() {
        try {
            if (user == null) {
                showAlert("Error", "User session is invalid. Please sign in again.");
                logout();
                return;
            }

            nameErrorLabel.setText("");
            phoneErrorLabel.setText("");
            passwordErrorLabel.setText("");

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Validation
            if (name.isEmpty()) {
                nameErrorLabel.setText("Name cannot be empty");
                return;
            }
            if (name.length() > 50) {
                nameErrorLabel.setText("Name cannot exceed 50 characters");
                return;
            }
            if (!name.matches("[A-Za-z\\s]+")) {
                nameErrorLabel.setText("Name can only contain letters and spaces");
                return;
            }
            if (!phone.isEmpty() && !phone.matches("\\d{10,15}")) {
                phoneErrorLabel.setText("Phone must be 10-15 digits");
                return;
            }
            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    passwordErrorLabel.setText("Passwords do not match");
                    return;
                }
                if (newPassword.length() < 8) {
                    passwordErrorLabel.setText("Password must be at least 8 characters");
                    return;
                }
            }

            // Update user
            user.setName(name);
            user.setPhone(phone);
            user.setAddress(address);
            serviceUser.update(user);
            Session.getSession(user).setCurrentUser(Session.getSession(user));

            if (!newPassword.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                serviceUser.updatePassword(user.getId(), hashedPassword);
            }

            showAlert("Success", "Profile updated successfully");
            newPasswordField.clear();
            confirmPasswordField.clear();
            loadProfile();
        } catch (SQLException e) {
            showAlert("Database Error", "Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteAccount() {
        try {
            if (user == null) {
                showAlert("Error", "User session is invalid. Please sign in again.");
                logout();
                return;
            }

            // Show confirmation dialog
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Account");
            confirmation.setHeaderText("Are you sure you want to delete your agency account?");
            confirmation.setContentText("This action cannot be undone. All your data will be permanently removed.");
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete user and token
                serviceUser.delete(user.getId());
                Session.clearSession();

                // Redirect to sign-in page
                Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/signin.fxml"));
                Stage stage = (Stage) btnDeleteAccount.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

                showAlert("Success", "Your agency account has been deleted.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error deleting account: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Error", "Failed to delete account.");
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
            showAlert("Error", "Failed to log out");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}