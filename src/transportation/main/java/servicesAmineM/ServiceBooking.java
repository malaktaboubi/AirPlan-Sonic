package servicesAmineM;

import utilsAmineM.DBConnection2;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Date;

public class ServiceBooking {

    public static class TravelSummary {
        public int totalTrips;
        public int flightsTaken;
        public int accommodationsBooked;
        public int transportBooked;

        public TravelSummary(int totalTrips, int flightsTaken, int accommodationsBooked, int transportBooked) {
            this.totalTrips = totalTrips;
            this.flightsTaken = flightsTaken;
            this.accommodationsBooked = accommodationsBooked;
            this.transportBooked = transportBooked;
        }
    }

    public static class UpcomingReservation {
        public String flightDetails;
        public String accommodationInfo;
        public String transportInfo;
        public long daysUntilNextTrip;

        public UpcomingReservation(String flightDetails, String accommodationInfo, String transportInfo, long daysUntilNextTrip) {
            this.flightDetails = flightDetails;
            this.accommodationInfo = accommodationInfo;
            this.transportInfo = transportInfo;
            this.daysUntilNextTrip = daysUntilNextTrip;
        }
    }

    public static class BookingTrends {
        public Map<String, Integer> monthlyBookings;
        public String preferredSeason;
        public String topDestination;

        public BookingTrends(Map<String, Integer> monthlyBookings, String preferredSeason, String topDestination) {
            this.monthlyBookings = monthlyBookings;
            this.preferredSeason = preferredSeason;
            this.topDestination = topDestination;
        }
    }

    public static class SpendingOverview {
        public double totalSpent;
        public double averageCostPerTrip;
        public String mostExpensiveTrip;
        public Map<String, Double> spendingBreakdown;

        public SpendingOverview(double totalSpent, double averageCostPerTrip, String mostExpensiveTrip, Map<String, Double> spendingBreakdown) {
            this.totalSpent = totalSpent;
            this.averageCostPerTrip = averageCostPerTrip;
            this.mostExpensiveTrip = mostExpensiveTrip;
            this.spendingBreakdown = spendingBreakdown;
        }
    }

    public static class PreferencesHabits {
        public String favoriteAirline;
        public String favoriteAccommodation;
        public String favoriteTransport;
        public String mostUsedClass;
        public double averageTripDuration;
        public double soloVsGroupRatio;

        public PreferencesHabits(String favoriteAirline, String favoriteAccommodation, String favoriteTransport,
                                 String mostUsedClass, double averageTripDuration, double soloVsGroupRatio) {
            this.favoriteAirline = favoriteAirline;
            this.favoriteAccommodation = favoriteAccommodation;
            this.favoriteTransport = favoriteTransport;
            this.mostUsedClass = mostUsedClass;
            this.averageTripDuration = averageTripDuration;
            this.soloVsGroupRatio = soloVsGroupRatio;
        }
    }

    public static class FeedbackActivity {
        public int feedbackCount;
        public double responseRate;
        public String mostCommonFeedbackType;

        public FeedbackActivity(int feedbackCount, double responseRate, String mostCommonFeedbackType) {
            this.feedbackCount = feedbackCount;
            this.responseRate = responseRate;
            this.mostCommonFeedbackType = mostCommonFeedbackType;
        }
    }

    public static class ProgramParticipation {
        public List<String> activePrograms;
        public String programHistory;
        public String benefitsUnlocked;

        public ProgramParticipation(List<String> activePrograms, String programHistory, String benefitsUnlocked) {
            this.activePrograms = activePrograms;
            this.programHistory = programHistory;
            this.benefitsUnlocked = benefitsUnlocked;
        }
    }

    public TravelSummary getTravelSummary(int userId) throws SQLException {
        String sql = "SELECT type, COUNT(*) as count FROM bookings WHERE user_id = ? AND status != 'CANCELLED' GROUP BY type";
        int flights = 0, accommodations = 0, transport = 0;

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                switch (rs.getString("type")) {
                    case "FLIGHT": flights = rs.getInt("count"); break;
                    case "ACCOMMODATION": accommodations = rs.getInt("count"); break;
                    case "TRANSPORT": transport = rs.getInt("count"); break;
                }
            }
        }
        int totalTrips = flights + accommodations + transport;
        return new TravelSummary(totalTrips, flights, accommodations, transport);
    }

    public UpcomingReservation getUpcomingReservation(int userId) throws SQLException {
        String sql = "SELECT b.type, b.start_date, b.destination, f.flight_number, f.departure_time, " +
                "a.hotel_name, a.check_in_date, t.type as transport_type, t.pickup_time " +
                "FROM bookings b " +
                "LEFT JOIN flights f ON b.flight_id = f.id " +
                "LEFT JOIN accommodations a ON b.accommodation_id = a.id " +
                "LEFT JOIN transport t ON b.transport_id = t.id " +
                "WHERE b.user_id = ? AND b.status = 'UPCOMING' AND b.start_date >= CURDATE() " +
                "ORDER BY b.start_date ASC LIMIT 1";

        String flightDetails = "No upcoming flights";
        String accommodationInfo = "No upcoming accommodations";
        String transportInfo = "No upcoming transport";
        long daysUntilNextTrip = -1;

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                daysUntilNextTrip = ChronoUnit.DAYS.between(LocalDate.now(), startDate);

                switch (rs.getString("type")) {
                    case "FLIGHT":
                        flightDetails = String.format("Flight %s to %s on %s",
                                rs.getString("flight_number"),
                                rs.getString("destination"),
                                rs.getTimestamp("departure_time"));
                        break;
                    case "ACCOMMODATION":
                        accommodationInfo = String.format("Hotel %s in %s on %s",
                                rs.getString("hotel_name"),
                                rs.getString("destination"),
                                rs.getDate("check_in_date"));
                        break;
                    case "TRANSPORT":
                        transportInfo = String.format("%s at %s on %s",
                                rs.getString("transport_type"),
                                rs.getString("destination"),
                                rs.getTimestamp("pickup_time"));
                        break;
                }
            }
        }
        return new UpcomingReservation(flightDetails, accommodationInfo, transportInfo, daysUntilNextTrip);
    }

    public BookingTrends getBookingTrends(int userId) throws SQLException {
        String sql = "SELECT destination, MONTH(booking_date) as month, COUNT(*) as count " +
                "FROM bookings WHERE user_id = ? AND status != 'CANCELLED' " +
                "GROUP BY destination, MONTH(booking_date)";

        Map<String, Integer> monthlyBookings = new HashMap<>();
        Map<String, Integer> destinationCounts = new HashMap<>();
        Map<Integer, Integer> monthCounts = new HashMap<>();

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int month = rs.getInt("month");
                String monthName = Month.of(month).name();
                int count = rs.getInt("count");
                String destination = rs.getString("destination");

                monthlyBookings.merge(monthName, count, Integer::sum);
                destinationCounts.merge(destination, count, Integer::sum);
                monthCounts.merge(month, count, Integer::sum);
            }
        }

        String preferredSeason = "Unknown";
        if (!monthCounts.isEmpty()) {
            int maxMonth = monthCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(1);
            preferredSeason = switch (maxMonth) {
                case 12, 1, 2 -> "Winter";
                case 3, 4, 5 -> "Spring";
                case 6, 7, 8 -> "Summer";
                case 9, 10, 11 -> "Fall";
                default -> "Unknown";
            };
        }

        String topDestination = destinationCounts.isEmpty() ? "None" :
                destinationCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("None");

        return new BookingTrends(monthlyBookings, preferredSeason, topDestination);
    }

    public SpendingOverview getSpendingOverview(int userId) throws SQLException {
        String sql = "SELECT type, cost, destination, start_date FROM bookings WHERE user_id = ? AND status != 'CANCELLED'";
        double totalSpent = 0.0;
        int tripCount = 0;
        double maxCost = 0.0;
        String mostExpensiveTrip = "None";
        Map<String, Double> spendingBreakdown = new HashMap<>();
        spendingBreakdown.put("FLIGHT", 0.0);
        spendingBreakdown.put("ACCOMMODATION", 0.0);
        spendingBreakdown.put("TRANSPORT", 0.0);

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double cost = rs.getDouble("cost");
                String type = rs.getString("type");
                totalSpent += cost;
                tripCount++;
                spendingBreakdown.merge(type, cost, Double::sum);

                if (cost > maxCost) {
                    maxCost = cost;
                    mostExpensiveTrip = String.format("%s to %s on %s", type, rs.getString("destination"), rs.getDate("start_date"));
                }
            }
        }

        double averageCostPerTrip = tripCount > 0 ? totalSpent / tripCount : 0.0;
        return new SpendingOverview(totalSpent, averageCostPerTrip, mostExpensiveTrip, spendingBreakdown);
    }

    public PreferencesHabits getPreferencesHabits(int userId) throws SQLException {
        String sql = "SELECT b.type, b.class_type, b.passengers, b.start_date, b.end_date, " +
                "f.airline, a.provider as accommodation_provider, t.provider as transport_provider " +
                "FROM bookings b " +
                "LEFT JOIN flights f ON b.flight_id = f.id " +
                "LEFT JOIN accommodations a ON b.accommodation_id = a.id " +
                "LEFT JOIN transport t ON b.transport_id = t.id " +
                "WHERE b.user_id = ? AND b.status != 'CANCELLED'";

        Map<String, Integer> airlineCounts = new HashMap<>();
        Map<String, Integer> accommodationCounts = new HashMap<>();
        Map<String, Integer> transportCounts = new HashMap<>();
        Map<String, Integer> classCounts = new HashMap<>();
        double totalDuration = 0.0;
        int durationCount = 0;
        int soloTrips = 0, groupTrips = 0;

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String airline = rs.getString("airline");
                String accommodation = rs.getString("accommodation_provider");
                String transport = rs.getString("transport_provider");
                String classType = rs.getString("class_type");
                int passengers = rs.getInt("passengers");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");

                if (airline != null) airlineCounts.merge(airline, 1, Integer::sum);
                if (accommodation != null) accommodationCounts.merge(accommodation, 1, Integer::sum);
                if (transport != null) transportCounts.merge(transport, 1, Integer::sum);
                if (classType != null) classCounts.merge(classType, 1, Integer::sum);

                if (passengers == 1) soloTrips++;
                else if (passengers > 1) groupTrips++;

                if (startDate != null && endDate != null) {
                    long duration = ChronoUnit.DAYS.between(
                            ((java.sql.Date) startDate).toLocalDate(), ((java.sql.Date) endDate).toLocalDate()) + 1;
                    totalDuration += duration;
                    durationCount++;
                }
            }
        }

        String favoriteAirline = airlineCounts.isEmpty() ? "None" :
                airlineCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse("None");
        String favoriteAccommodation = accommodationCounts.isEmpty() ? "None" :
                accommodationCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse("None");
        String favoriteTransport = transportCounts.isEmpty() ? "None" :
                transportCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse("None");
        String mostUsedClass = classCounts.isEmpty() ? "None" :
                classCounts.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse("None");

        double averageTripDuration = durationCount > 0 ? totalDuration / durationCount : 0.0;
        double soloVsGroupRatio = groupTrips > 0 ? (double) soloTrips / groupTrips : (soloTrips > 0 ? 1.0 : 0.0);

        return new PreferencesHabits(favoriteAirline, favoriteAccommodation, favoriteTransport,
                mostUsedClass, averageTripDuration, soloVsGroupRatio);
    }

    public FeedbackActivity getFeedbackActivity(int userId) throws SQLException {
        String sql = "SELECT type, response FROM feedback WHERE user_id = ?";
        int feedbackCount = 0;
        int respondedCount = 0;
        Map<String, Integer> feedbackTypes = new HashMap<>();

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                feedbackCount++;
                if (rs.getString("response") != null) respondedCount++;
                String type = rs.getString("type");
                feedbackTypes.merge(type, 1, Integer::sum);
            }
        }

        double responseRate = feedbackCount > 0 ? (double) respondedCount / feedbackCount * 100 : 0.0;
        String mostCommonType = feedbackTypes.isEmpty() ? "None" :
                feedbackTypes.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse("None");

        return new FeedbackActivity(feedbackCount, responseRate, mostCommonType);
    }

    public ProgramParticipation getProgramParticipation(int userId) throws SQLException {
        String sql = "SELECT program_name, status, join_date, benefits FROM loyalty_programs WHERE user_id = ?";
        List<String> activePrograms = new ArrayList<>();
        List<String> programHistory = new ArrayList<>();
        List<String> benefits = new ArrayList<>();

        try (Connection conn = DBConnection2.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String programName = rs.getString("program_name");
                String status = rs.getString("status");
                String benefitsText = rs.getString("benefits");

                if (status.equals("ACTIVE")) activePrograms.add(programName);
                programHistory.add(String.format("%s (%s, joined %s)", programName, status, rs.getDate("join_date")));
                if (benefitsText != null) benefits.add(benefitsText);
            }
        }

        return new ProgramParticipation(
                activePrograms,
                programHistory.isEmpty() ? "None" : String.join("; ", programHistory),
                benefits.isEmpty() ? "None" : String.join("; ", benefits)
        );
    }
}