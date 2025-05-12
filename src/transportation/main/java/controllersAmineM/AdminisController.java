package controllersAmineM;

/*
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.text.DecimalFormat;
*/
public class AdminisController /*implements Initializable*/ {

    /*@FXML private BorderPane root;
    @FXML private Label adminNameLabel;
    @FXML private Button btnLogout;
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label userTypesLabel;
    @FXML private Label topUsersLabel;
    @FXML private Label newUsersLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label reservationsByTypeLabel;
    @FXML private Label successCancellationLabel;
    @FXML private Label peakUsageLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label revenueByServiceLabel;
    @FXML private Label topEarnersLabel;
    @FXML private Label totalFeedbackLabel;
    @FXML private Label feedbackTypesLabel;
    @FXML private Label recurringIssuesLabel;
    @FXML private Label unresolvedFeedbackLabel;
    @FXML private Label totalProgramsLabel;
    @FXML private Label popularProgramsLabel;
    @FXML private Label upcomingProgramsLabel;
    @FXML private Label programEngagementLabel;
    @FXML private Label failedBookingsLabel;
    @FXML private Label dataIssuesLabel;
    @FXML private Label lowAvailabilityLabel;
    @FXML private LineChart<String, Number> bookingChart;
    @FXML private CategoryAxis bookingMonthAxis;
    @FXML private NumberAxis bookingAxis;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis revenueMonthAxis;
    @FXML private NumberAxis revenueAxis;

    private User user;
    private ServiceAdmin serviceAdmin;
    public static final DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceAdmin = new ServiceAdmin();
        root.getStylesheets().add(getClass().getResource("/Profile.css").toExternalForm());
        user = Session.getCurrentUser();
        System.out.println("AdminDashboardController: User from session: " + (user != null ? user.getName() : "null"));
        if (user != null && "ADMIN".equals(user.getUserType())) {
            loadDashboard();
        } else {
            showAlert("Session Error", "Invalid user session. Please sign in as admin.");
            logout();
        }
    }

    private void loadDashboard() {
        try {
            adminNameLabel.setText("Admin: " + user.getName());

            // User Overview
            ServiceAdmin.UserOverview userOverview = serviceAdmin.getUserOverview();
            totalUsersLabel.setText("Total Users: " + userOverview.totalUsers);
            activeUsersLabel.setText("Active Users This Month: " + userOverview.activeUsers);
            userTypesLabel.setText("User Types: " + userOverview.userTypes);
            topUsersLabel.setText("Top Users: " + String.join(", ", userOverview.topUsers));
            newUsersLabel.setText("New Users This Month: " + userOverview.newUsers);

            // Platform Activity
            ServiceAdmin.PlatformActivity activity = serviceAdmin.getPlatformActivity();
            totalReservationsLabel.setText("Total Reservations: " + activity.totalReservations);
            reservationsByTypeLabel.setText("Reservations by Type: " + activity.reservationsByType);
            successCancellationLabel.setText("Success vs Cancellation: " + df.format(activity.successRate) + "% / " + df.format(activity.cancellationRate) + "%");
            peakUsageLabel.setText("Peak Usage: " + activity.peakUsage);

            // Financial Overview
            ServiceAdmin.FinancialOverview financial = serviceAdmin.getFinancialOverview();
            totalRevenueLabel.setText("Total Revenue: $" + df.format(financial.totalRevenue));
            revenueByServiceLabel.setText("Revenue by Service: " + financial.revenueByService);
            topEarnersLabel.setText("Top Earners: " + String.join(", ", financial.topEarners));


            // Feedback & Satisfaction
            ServiceAdmin.FeedbackSatisfaction feedback = serviceAdmin.getFeedbackSatisfaction();
            totalFeedbackLabel.setText("Total Feedback: " + feedback.totalFeedback);
            feedbackTypesLabel.setText("Feedback Types: " + feedback.feedbackTypes);
            recurringIssuesLabel.setText("Top Issues: " + String.join(", ", feedback.recurringIssues));
            unresolvedFeedbackLabel.setText("Unresolved Feedback: " + feedback.unresolvedFeedback);

            // Program Participation
            ServiceAdmin.ProgramParticipation programs = serviceAdmin.getProgramParticipation();
            totalProgramsLabel.setText("Programs Created: " + programs.totalPrograms);
            popularProgramsLabel.setText("Most Enrolled Programs: " + String.join(", ", programs.popularPrograms));
            upcomingProgramsLabel.setText("Upcoming/Ending Programs: " + programs.upcomingPrograms);
            programEngagementLabel.setText("User Engagement: " + df.format(programs.engagementRate) + "%");

            // System Health & Warnings
            ServiceAdmin.SystemHealth health = serviceAdmin.getSystemHealth();
            failedBookingsLabel.setText("Failed Bookings: " + health.failedBookings);
            dataIssuesLabel.setText("Data Issues: " + health.dataIssues);
            lowAvailabilityLabel.setText("Low Availability Services: " + health.lowAvailability);

            // Booking Chart
            XYChart.Series<String, Number> bookingSeries = new XYChart.Series<>();
            bookingSeries.setName("Bookings");
            activity.bookingsPerMonth.forEach((month, count) -> bookingSeries.getData().add(new XYChart.Data<>(month, count)));
            bookingChart.getData().clear();
            bookingChart.getData().add(bookingSeries);

            // Revenue Chart
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue");
            financial.monthlyRevenue.forEach((month, amount) -> revenueSeries.getData().add(new XYChart.Data<>(month, amount)));
            revenueChart.getData().clear();
            revenueChart.getData().add(revenueSeries);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setUser(User user) {
        this.user = user;
        System.out.println("AdminisController: setUser called with: " + (user != null ? user.getName() : "null"));
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/
}