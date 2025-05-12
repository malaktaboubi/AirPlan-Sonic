package controllersAmineM;

import entitiesAmineM.User;


import controllersAmineM.AgencyController2;
import entitiesAmineM.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.text.DecimalFormat;

public class AgencyDashboardController /*implements Initializable*/ {

    /*@FXML private BorderPane root;
    @FXML private Label agencyNameLabel;
    @FXML private Button btnProfile;
    @FXML private Button btnLogout;
    @FXML private Label totalOffersLabel;
    @FXML private Label activeVsExpiredLabel;
    @FXML private Label popularOfferTypeLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label averagePriceLabel;
    @FXML private Label mostProfitableServiceLabel;
    @FXML private Label topOffersLabel;
    @FXML private Label occupancyRateLabel;
    @FXML private Label cancelledBookingsLabel;
    @FXML private Label feedbackPolarityLabel;
    @FXML private Label peakPeriodsLabel;
    @FXML private Label leadTimeLabel;
    @FXML private Label totalFeedbackLabel;
    @FXML private Label feedbackByTypeLabel;
    @FXML private Label commonFeedbackLabel;
    @FXML private Label ratingTrendsLabel;
    @FXML private Label conversionRateLabel;
    @FXML private Label programsJoinedLabel;
    @FXML private Label retentionRateLabel;
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private CategoryAxis revenueMonthAxis;
    @FXML private NumberAxis revenueAxis;
    @FXML private BarChart<String, Number> bookingChart;
    @FXML private CategoryAxis bookingMonthAxis;
    @FXML private NumberAxis bookingAxis;

    private entitiesAmineM.User user;
    private ServiceAgency serviceAgency;
    private static final DecimalFormat df = new DecimalFormat("#.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceAgency = new ServiceAgency();
        root.getStylesheets().add(getClass().getResource("/Profile.css").toExternalForm());
        user = Session.getCurrentUser();
        System.out.println("AgencyDashboardController: User from session: " + (user != null ? user.getName() : "null"));
        if (user != null) {
            loadDashboard();
        } else {
            showAlert("Session Error", "No user session found. Please sign in again.");
            redirectToSignin();
        }
    }

    private void loadDashboard() {
        try {
            agencyNameLabel.setText("Agency: " + user.getName());

            // Business Overview
            ServiceAgency.BusinessOverview overview = serviceAgency.getBusinessOverview(user.getId());
            totalOffersLabel.setText("Total Offers Published: " + overview.totalOffers);
            activeVsExpiredLabel.setText("Active vs Expired: " + overview.activeOffers + " / " + overview.expiredOffers);
            popularOfferTypeLabel.setText("Most Popular Offer Type: " + overview.mostPopularOfferType);
            totalReservationsLabel.setText("Total Reservations: " + overview.totalReservations);

            // Revenue Insights
            ServiceAgency.RevenueInsights revenue = serviceAgency.getRevenueInsights(user.getId());
            totalIncomeLabel.setText("Total Income: $" + df.format(revenue.totalIncome));
            averagePriceLabel.setText("Average Price per Reservation: $" + df.format(revenue.averagePrice));
            mostProfitableServiceLabel.setText("Most Profitable Service: " + revenue.mostProfitableService);

            // Offer Performance
            ServiceAgency.OfferPerformance performance = serviceAgency.getOfferPerformance(user.getId());
            topOffersLabel.setText("Top 5 Offers: " + String.join(", ", performance.topOffers));
            occupancyRateLabel.setText("Occupancy Rate: " + df.format(performance.occupancyRate) + "%");
            cancelledBookingsLabel.setText("Cancelled Bookings: " + performance.cancelledBookings);
            feedbackPolarityLabel.setText("Feedback: " + performance.positiveFeedback + " Positive / " + performance.negativeFeedback + " Negative");

            // Time-Based Insights
            ServiceAgency.TimeBasedInsights timeInsights = serviceAgency.getTimeBasedInsights(user.getId());
            peakPeriodsLabel.setText("Peak Booking Periods: " + timeInsights.peakPeriods);
            leadTimeLabel.setText("Average Lead Time: " + df.format(timeInsights.averageLeadTime) + " days");

            // Customer Feedback Overview
            ServiceAgency.CustomerFeedback feedback = serviceAgency.getCustomerFeedback(user.getId());
            totalFeedbackLabel.setText("Total Feedback: " + feedback.totalFeedback);
            feedbackByTypeLabel.setText("Feedback by Type: " + feedback.feedbackByType);
            commonFeedbackLabel.setText("Common Issues/Praises: " + feedback.commonFeedback);
            ratingTrendsLabel.setText("Average Rating: " + df.format(feedback.averageRating));

            // Marketing & Engagement
            ServiceAgency.MarketingEngagement engagement = serviceAgency.getMarketingEngagement(user.getId());
            conversionRateLabel.setText("Conversion Rate: " + df.format(engagement.conversionRate) + "%");
            programsJoinedLabel.setText("Programs Joined: " + engagement.programsJoined);
            retentionRateLabel.setText("Customer Retention Rate: " + df.format(engagement.retentionRate) + "%");

            // Revenue Chart
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Revenue");
            revenue.monthlyRevenue.forEach((month, amount) -> revenueSeries.getData().add(new XYChart.Data<>(month, amount)));
            revenueChart.getData().clear();
            revenueChart.getData().add(revenueSeries);

            // Booking Chart
            XYChart.Series<String, Number> bookingSeries = new XYChart.Series<>();
            bookingSeries.setName("Bookings");
            timeInsights.bookingsPerMonth.forEach((month, count) -> bookingSeries.getData().add(new XYChart.Data<>(month, count)));
            bookingChart.getData().clear();
            bookingChart.getData().add(bookingSeries);
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToProfile() {
        try {
            if (user == null) {
                showAlert("Session Error", "No user session found. Please sign in again.");
                redirectToSignin();
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ProfileAgency.fxml"));
            Parent root = loader.load();
            AgencyController2 controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) btnProfile.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load profile: " + e.getMessage());
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

    private void redirectToSignin() {
        try {
            Session.clearSession();
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/signin.fxml"));
            Stage stage = (Stage) btnProfile.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to redirect to sign-in: " + e.getMessage());
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