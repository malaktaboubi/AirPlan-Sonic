package controllersAmineM;
/*
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
*/
public class ClientDashboardController /*implements Initializable*/ {

   /* private static final Logger LOGGER = Logger.getLogger(ClientDashboardController.class.getName());
    private static final DecimalFormat DF = new DecimalFormat("#.00");

    @FXML private BorderPane root;
    @FXML private Label clientNameLabel;
    @FXML private Button btnProfile, btnLogout;
    @FXML private Label totalTripsLabel, flightsTakenLabel, accommodationsBookedLabel, transportBookedLabel;
    @FXML private Label totalSpentLabel, averageCostLabel, mostExpensiveTripLabel, spendingBreakdownLabel;
    @FXML private Label nextFlightLabel, nextAccommodationLabel, nextTransportLabel, daysUntilTripLabel;
    @FXML private Label favoriteAirlineLabel, favoriteAccommodationLabel, favoriteTransportLabel, mostUsedClassLabel;
    @FXML private Label averageTripDurationLabel, soloVsGroupLabel;
    @FXML private Label feedbackCountLabel, responseRateLabel, commonFeedbackTypeLabel;
    @FXML private Label activeProgramsLabel, programHistoryLabel, benefitsUnlockedLabel;
    @FXML private Label preferredSeasonLabel, topDestinationLabel;
    @FXML private BarChart<String, Number> bookingChart;
    @FXML private CategoryAxis monthAxis;
    @FXML private NumberAxis countAxis;

    private User user;
    private ServiceBooking serviceBooking;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceBooking = new ServiceBooking();
        root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        user = Session.getCurrentUser();
        if (user != null) {
            loadDashboard();
        } else {
            redirectToSignin();
        }
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            loadDashboard();
        } else {
            redirectToSignin();
        }
    }

    private void loadDashboard() {
        try {
            clientNameLabel.setText("Client: " + user.getName());

            // Travel Summary
            ServiceBooking.TravelSummary summary = serviceBooking.getTravelSummary(user.getId());
            totalTripsLabel.setText("Total Trips Booked: " + summary.totalTrips);
            flightsTakenLabel.setText("Flights Taken: " + summary.flightsTaken);
            accommodationsBookedLabel.setText("Accommodations Booked: " + summary.accommodationsBooked);
            transportBookedLabel.setText("Transportation Bookings: " + summary.transportBooked);

            // Spending Overview
            ServiceBooking.SpendingOverview spending = serviceBooking.getSpendingOverview(user.getId());
            totalSpentLabel.setText("Total Spent: $" + DF.format(spending.totalSpent));
            averageCostLabel.setText("Average Cost per Trip: $" + DF.format(spending.averageCostPerTrip));
            mostExpensiveTripLabel.setText("Most Expensive Trip: " + spending.mostExpensiveTrip);
            spendingBreakdownLabel.setText(String.format("Spending Breakdown: Flights: $%s, Accommodations: $%s, Transport: $%s",
                    DF.format(spending.spendingBreakdown.getOrDefault("FLIGHT", 0.0)),
                    DF.format(spending.spendingBreakdown.getOrDefault("ACCOMMODATION", 0.0)),
                    DF.format(spending.spendingBreakdown.getOrDefault("TRANSPORT", 0.0))));

            // Upcoming Reservations
            ServiceBooking.UpcomingReservation reservation = serviceBooking.getUpcomingReservation(user.getId());
            nextFlightLabel.setText("Next Flight: " + reservation.flightDetails);
            nextAccommodationLabel.setText("Next Accommodation: " + reservation.accommodationInfo);
            nextTransportLabel.setText("Next Transport: " + reservation.transportInfo);
            daysUntilTripLabel.setText("Days Until Next Trip: " + (reservation.daysUntilNextTrip >= 0 ? reservation.daysUntilNextTrip : "N/A"));

            // Preferences & Habits
            ServiceBooking.PreferencesHabits preferences = serviceBooking.getPreferencesHabits(user.getId());
            favoriteAirlineLabel.setText("Favorite Airline: " + (preferences.favoriteAirline != null ? preferences.favoriteAirline : "None"));
            favoriteAccommodationLabel.setText("Favorite Accommodation: " + (preferences.favoriteAccommodation != null ? preferences.favoriteAccommodation : "None"));
            favoriteTransportLabel.setText("Favorite Transport: " + (preferences.favoriteTransport != null ? preferences.favoriteTransport : "None"));
            mostUsedClassLabel.setText("Most Used Class: " + (preferences.mostUsedClass != null ? preferences.mostUsedClass : "None"));
            averageTripDurationLabel.setText("Average Trip Duration: " + DF.format(preferences.averageTripDuration) + " days");
            soloVsGroupLabel.setText("Solo vs Group Ratio: " + DF.format(preferences.soloVsGroupRatio));

            // Feedback Activity
            ServiceBooking.FeedbackActivity feedback = serviceBooking.getFeedbackActivity(user.getId());
            feedbackCountLabel.setText("Feedback Submitted: " + feedback.feedbackCount);
            responseRateLabel.setText("Response Rate: " + DF.format(feedback.responseRate) + "%");
            commonFeedbackTypeLabel.setText("Most Common Feedback: " + (feedback.mostCommonFeedbackType != null ? feedback.mostCommonFeedbackType : "None"));

            // Program Participation
            ServiceBooking.ProgramParticipation programs = serviceBooking.getProgramParticipation(user.getId());
            activeProgramsLabel.setText("Active Programs: " + (programs.activePrograms.isEmpty() ? "None" : String.join(", ", programs.activePrograms)));
            programHistoryLabel.setText("Program History: " + (programs.programHistory != null ? programs.programHistory : "None"));
            benefitsUnlockedLabel.setText("Benefits Unlocked: " + (programs.benefitsUnlocked != null ? programs.benefitsUnlocked : "None"));

            // Booking Trends
            ServiceBooking.BookingTrends trends = serviceBooking.getBookingTrends(user.getId());
            preferredSeasonLabel.setText("Preferred Season: " + trends.preferredSeason);
            topDestinationLabel.setText("Most Visited Destination: " + trends.topDestination);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Bookings");
            trends.monthlyBookings.forEach((month, count) -> series.getData().add(new XYChart.Data<>(month, count)));
            bookingChart.getData().clear();
            bookingChart.getData().add(series);
        } catch (SQLException e) {
            LOGGER.severe("Database error loading dashboard for user ID: " + user.getId() + ": " + e.getMessage());
            showAlert("Database Error", "Error loading dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ProfileClient.fxml"));
            Parent root = loader.load();
            ClientController2 controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) btnProfile.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Error navigating to profile for user ID: " + (user != null ? user.getId() : "null") + ": " + e.getMessage());
            showAlert("Error", "Failed to load profile: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void logout() {
        Session.clearSession();
        redirectToSignin();
    }

    private void redirectToSignin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/signin.fxml"));
            Stage stage = (Stage) this.root.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Error redirecting to sign-in: " + e.getMessage());
            showAlert("Error", "Failed to load sign-in page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }*/
}