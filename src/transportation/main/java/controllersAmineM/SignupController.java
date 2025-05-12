package controllersAmineM;

import entitiesAmineM.User;
import servicesAmineM.ServiceUser;
import servicesAmineM.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML private TextField username_signup_textfield;
    @FXML private TextField email_signup_textfield;
    @FXML private PasswordField password_signup_textfield;
    @FXML private PasswordField confirm_password_signup_textfield;
    @FXML private ChoiceBox<String> role_signup_choicebox;
    @FXML private CheckBox I_agree_signup;
    @FXML private Button btn_signup;
    @FXML private Button btn_signin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        role_signup_choicebox.setItems(FXCollections.observableArrayList("AGENCY", "CLIENT"));
        role_signup_choicebox.setValue("CLIENT");
    }

    @FXML
    private void btn_signup_clicked() {
        String username = username_signup_textfield.getText().trim();
        String email = email_signup_textfield.getText().trim();
        String password = password_signup_textfield.getText();
        String confirmPassword = confirm_password_signup_textfield.getText();
        String role = role_signup_choicebox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match");
            return;
        }

        if (!I_agree_signup.isSelected()) {
            showAlert("Error", "You must agree to the terms and privacy policy");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Error", "Invalid email format");
            return;
        }

        User user = new User(0, username, email, password, role, "", "", "", null);

        try {
            ServiceUser service = new ServiceUser();
            for (User existingUser : service.getAll()) {
                if (existingUser.getEmail().equals(email)) {
                    showAlert("Error", "Email already registered");
                    return;
                }
            }

            service.add(user);
            Session.setCurrentUser(Session.getSession(user));
            redirectToDashboard(user);
        } catch (SQLException e) {
            showAlert("Database Error", "Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btn_signin_clicked() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxmlAmineM/signin.fxml"));
            Stage stage = (Stage) btn_signin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load sign-in page");
            e.printStackTrace();
        }
    }

    private void redirectToDashboard(User user) {
        try {
            String fxmlPath = switch (user.getUserType()) {
                case "AGENCY" -> "/fxml/menu.fxml";
                case "CLIENT" -> "/fxml/menu.fxml";
                default -> throw new IllegalArgumentException("Unknown user type");
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SigninController.UserAwareController) {
                ((SigninController.UserAwareController) controller).setUser(user);
            }

            Stage stage = (Stage) btn_signup.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load dashboard");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}