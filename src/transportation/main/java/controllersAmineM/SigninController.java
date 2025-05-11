package controllersAmineM;

import entitiesAmineM.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import servicesAmineM.ServiceUser;
import servicesAmineM.Session;
import utilsAmineM.DBConnection2;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;
import java.util.ResourceBundle;

public class SigninController implements Initializable {

    @FXML private TextField email_signin_textfield;
    @FXML private PasswordField password_signin_textfield;
    @FXML private Button btn_signin;
    @FXML private Button btn_signup;
    @FXML private CheckBox remember_me;
    @FXML private Label forgot_password;

    private static final Preferences prefs = Preferences.userNodeForPackage(SigninController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String savedEmail = prefs.get("remembered_email", "");
        String savedToken = prefs.get("remembered_token", "");
        if (!savedEmail.isEmpty() && !savedToken.isEmpty()) {
            try {
                ServiceUser service = new ServiceUser();
                User user = service.verifyRememberMeToken(savedEmail, savedToken);
                if (user != null) {
                    email_signin_textfield.setText(savedEmail);
                    remember_me.setSelected(true);
                    Session.setCurrentUser(Session.getSession(user));
                    redirectToDashboard(user);
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Error verifying saved credentials: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btn_signin_clicked() {
        String email = email_signin_textfield.getText();
        String password = password_signin_textfield.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        try {
            User user = authenticateUser(email, password);
            if (user != null) {
                Session.setCurrentUser(Session.getSession(user));

                ServiceUser service = new ServiceUser();
                if (remember_me.isSelected()) {
                    String token = UUID.randomUUID().toString();
                    service.storeRememberMeToken(user.getId(), token);
                    prefs.put("remembered_email", email);
                    prefs.put("remembered_token", token);
                } else {
                    service.clearRememberMeToken(user.getId());
                    prefs.remove("remembered_email");
                    prefs.remove("remembered_token");
                }

                redirectToDashboard(user);
            } else {
                showAlert("Login Failed", "Invalid email or password");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error accessing user data");
            e.printStackTrace();
        }
    }

    @FXML
    private void btn_signup_clicked() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxmlAmineM/signup.fxml"));
            Stage stage = (Stage) btn_signup.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load sign-up page");
            e.printStackTrace();
        }
    }

    @FXML
    private void forgot_password_clicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Enter your email address");
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(email -> {
            try {
                ServiceUser service = new ServiceUser();
                List<User> users = service.getAll();
                boolean userExists = users.stream().anyMatch(u -> u.getEmail().equals(email));
                if (userExists) {
                    showAlert("Success", "A password reset link has been sent to " + email);
                } else {
                    showAlert("Error", "No account found with this email");
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Error checking user data");
                e.printStackTrace();
            }
        });
    }

    private User authenticateUser(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return getUser(email, password, pstmt);
        }
    }

    public static User getUser(String email, String password, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, email);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            hashedPassword,
                            rs.getString("user_type"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("passport"),
                            null
                    );
                }
            }
        }
        return null;
    }

    private void redirectToDashboard(User user) {
        try {
            String fxmlPath = switch (user.getUserType()) {
                case "ADMIN" -> "/fxml/menu.fxml";
                case "AGENCY" -> "/fxml/menu.fxml";
                case "CLIENT" -> "/fxml/menu.fxml";
                default -> throw new IllegalArgumentException("Unknown user type");
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                System.out.println("SigninController: Setting user: " + user.getName());
                ((UserAwareController) controller).setUser(user);
            } else {
                System.out.println("SigninController: Controller is not UserAwareController: " + controller.getClass().getName());
            }

            Stage stage = (Stage) btn_signin.getScene().getWindow();
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

    public interface UserAwareController {
        void setUser(User user);
    }
}