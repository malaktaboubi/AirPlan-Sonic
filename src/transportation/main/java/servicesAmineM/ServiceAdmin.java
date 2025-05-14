package servicesAmineM;

import java.sql.*;
import java.util.*;

import static controllersAmineM.AdminDashboardController.df;


public class ServiceAdmin {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/maalejdb";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public static class UserOverview {
        public int totalUsers;
        public int activeUsers;
        public String userTypes;
        public List<String> topUsers;
        public int newUsers;
    }

    public UserOverview getTotalUsers() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Total Users
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) overview.totalUsers = rs.getInt(1);
        }
        return overview;
    }
    public UserOverview getActiveUsersThisMonth() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Active Users This Month
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT b.user_id) FROM bookings b " +
                            "WHERE b.booking_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)"
            );
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) overview.activeUsers = rs.getInt(1);
        }
        return overview;
    }
    public UserOverview getUserTypes() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // User Types
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user_type, COUNT(*) as count FROM users GROUP BY user_type"
            );
            ResultSet rs = stmt.executeQuery();
            StringBuilder types = new StringBuilder();
            while (rs.next()) {
                types.append(rs.getString("user_type")).append(": ").append(rs.getInt("count")).append(", ");
            }
            overview.userTypes = types.length() > 0 ? types.substring(0, types.length() - 2) : "None";
        }
        return overview;
    }
    public UserOverview getTopUsers() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Top Users
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT u.name, COUNT(b.id) as bookings " +
                            "FROM users u JOIN bookings b ON u.id = b.id " +
                            "GROUP BY u.id, u.name ORDER BY bookings DESC LIMIT 5"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                overview.topUsers.add(rs.getString("name"));
            }
            if (overview.topUsers.isEmpty()) overview.topUsers.add("None");
        }
        return overview;
    }
    public UserOverview getNewUsersThisMonth() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // New Users This Month
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)"
            );
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) overview.newUsers = rs.getInt(1);
        }
        return overview;
    }
    public UserOverview getUserOverview() throws SQLException {
        UserOverview overview = new UserOverview();
        overview.topUsers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Total Users
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) overview.totalUsers = rs.getInt(1);

            // Active Users This Month
            stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT b.user_id) FROM bookings b " +
                            "WHERE b.booking_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)"
            );
            rs = stmt.executeQuery();
            if (rs.next()) overview.activeUsers = rs.getInt(1);

            // User Types
            stmt = conn.prepareStatement(
                    "SELECT user_type, COUNT(*) as count FROM users GROUP BY user_type"
            );
            rs = stmt.executeQuery();
            StringBuilder types = new StringBuilder();
            while (rs.next()) {
                types.append(rs.getString("user_type")).append(": ").append(rs.getInt("count")).append(", ");
            }
            overview.userTypes = types.length() > 0 ? types.substring(0, types.length() - 2) : "None";

            // Top Users
            stmt = conn.prepareStatement(
                    "SELECT u.name, COUNT(b.id) as bookings " +
                            "FROM users u JOIN bookings b ON u.id = b.id " +
                            "GROUP BY u.id, u.name ORDER BY bookings DESC LIMIT 5"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                overview.topUsers.add(rs.getString("name"));
            }
            if (overview.topUsers.isEmpty()) overview.topUsers.add("None");

            // New Users This Month
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE registration_date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)"
            );
            rs = stmt.executeQuery();
            if (rs.next()) overview.newUsers = rs.getInt(1);
        }
        return overview;
    }

    public static class PlatformActivity {
        public int totalReservations;
        public String reservationsByType;
        public double successRate;
        public double cancellationRate;
        public String peakUsage;
        public Map<String, Integer> bookingsPerMonth;
    }

    public PlatformActivity getPlatformActivity() throws SQLException {
        PlatformActivity activity = new PlatformActivity();
        activity.bookingsPerMonth = new LinkedHashMap<>();
        try (Connection conn = getConnection()) {
            // Total Reservations
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM bookings");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) activity.totalReservations = rs.getInt(1);

            // Reservations by Type
            stmt = conn.prepareStatement(
                    "SELECT o.type, COUNT(b.id) as count " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id GROUP BY o.type"
            );
            rs = stmt.executeQuery();
            StringBuilder types = new StringBuilder();
            while (rs.next()) {
                types.append(rs.getString("type")).append(": ").append(rs.getInt("count")).append(", ");
            }
            activity.reservationsByType = types.length() > 0 ? types.substring(0, types.length() - 2) : "None";

            // Success vs Cancellation Rate
            stmt = conn.prepareStatement(
                    "SELECT SUM(CASE WHEN status = 'CONFIRMED' THEN 1 ELSE 0 END) as success, " +
                            "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled " +
                            "FROM bookings"
            );
            rs = stmt.executeQuery();
            if (rs.next()) {
                int success = rs.getInt("success");
                int cancelled = rs.getInt("cancelled");
                int total = success + cancelled;
                activity.successRate = total > 0 ? (success * 100.0 / total) : 0.0;
                activity.cancellationRate = total > 0 ? (cancelled * 100.0 / total) : 0.0;
            }

            // Peak Usage
            stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(booking_date, '%W %H') as time, COUNT(*) as bookings " +
                            "FROM bookings GROUP BY time ORDER BY bookings DESC LIMIT 1"
            );
            rs = stmt.executeQuery();
            activity.peakUsage = rs.next() ? rs.getString("time") : "None";

            // Bookings per Month
            stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(booking_date, '%Y-%m') as month, COUNT(*) as bookings " +
                            "FROM bookings GROUP BY month ORDER BY month"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                activity.bookingsPerMonth.put(rs.getString("month"), rs.getInt("bookings"));
            }
        }
        return activity;
    }

    public static class FinancialOverview {
        public double totalRevenue;
        public String revenueByService;
        public List<String> topEarners;
        public Map<String, Double> monthlyRevenue;
    }

    public FinancialOverview getFinancialOverview() throws SQLException {
        FinancialOverview financial = new FinancialOverview();
        financial.topEarners = new ArrayList<>();
        financial.monthlyRevenue = new LinkedHashMap<>();
        try (Connection conn = getConnection()) {
            // Total Revenue
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT SUM(cost) FROM bookings WHERE status = 'CONFIRMED'"
            );
            ResultSet rs = stmt.executeQuery();
            financial.totalRevenue = rs.next() ? rs.getDouble(1) : 0.0;

            // Revenue by Service
            stmt = conn.prepareStatement(
                    "SELECT o.type, SUM(b.cost) as total " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE b.status = 'CONFIRMED' GROUP BY o.type"
            );
            rs = stmt.executeQuery();
            StringBuilder services = new StringBuilder();
            while (rs.next()) {
                services.append(rs.getString("type")).append(": $").append(df.format(rs.getDouble("total"))).append(", ");
            }
            financial.revenueByService = services.length() > 0 ? services.substring(0, services.length() - 2) : "None";

            // Top Earners (Agencies)
            stmt = conn.prepareStatement(
                    "SELECT u.name, SUM(b.cost) as total " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "JOIN users u ON o.agency_id = u.id " +
                            "WHERE b.status = 'CONFIRMED' GROUP BY u.id, u.name " +
                            "ORDER BY total DESC LIMIT 5"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                financial.topEarners.add(rs.getString("name"));
            }
            if (financial.topEarners.isEmpty()) financial.topEarners.add("None");

            // Monthly Revenue
            stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(booking_date, '%Y-%m') as month, SUM(cost) as total " +
                            "FROM bookings WHERE status = 'CONFIRMED' GROUP BY month ORDER BY month"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                financial.monthlyRevenue.put(rs.getString("month"), rs.getDouble("total"));
            }
        }
        return financial;
    }

    public static class ServiceMonitoring {
        public int totalServices;
        public List<String> popularServices;
        public int inactiveOffers;
        public double capacityUsage;
    }

    public ServiceMonitoring getServiceMonitoring() throws SQLException {
        ServiceMonitoring services = new ServiceMonitoring();
        services.popularServices = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Total Services
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM offers");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) services.totalServices = rs.getInt(1);

            // Popular Services
            stmt = conn.prepareStatement(
                    "SELECT o.details, COUNT(b.id) as bookings " +
                            "FROM offers o LEFT JOIN bookings b ON o.offer_id = b.offer_id " +
                            "GROUP BY o.offer_id, o.details ORDER BY bookings DESC LIMIT 5"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                services.popularServices.add(rs.getString("details"));
            }
            if (services.popularServices.isEmpty()) services.popularServices.add("None");

            // Inactive/Expired Offers
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM offers WHERE end_date < CURDATE()"
            );
            rs = stmt.executeQuery();
            if (rs.next()) services.inactiveOffers = rs.getInt(1);

            // Capacity Usage
            stmt = conn.prepareStatement(
                    "SELECT AVG(CAST(b.passengers AS DOUBLE) / o.capacity) * 100 as rate " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE b.status = 'CONFIRMED' AND o.capacity > 0"
            );
            rs = stmt.executeQuery();
            services.capacityUsage = rs.next() ? rs.getDouble("rate") : 0.0;
        }
        return services;
    }

    public static class FeedbackSatisfaction {
        public int totalFeedback;
        public String feedbackTypes;
        public List<String> recurringIssues;
        public int unresolvedFeedback;
    }

    public FeedbackSatisfaction getFeedbackSatisfaction() throws SQLException {
        FeedbackSatisfaction feedback = new FeedbackSatisfaction();
        feedback.recurringIssues = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Total Feedback
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM feedback");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) feedback.totalFeedback = rs.getInt(1);

            // Feedback Types
            stmt = conn.prepareStatement(
                    "SELECT CASE WHEN rating >= 4 THEN 'Praise' " +
                            "WHEN rating <= 2 THEN 'Complaint' ELSE 'Suggestion' END as type, COUNT(*) as count " +
                            "FROM feedback GROUP BY type"
            );
            rs = stmt.executeQuery();
            StringBuilder types = new StringBuilder();
            while (rs.next()) {
                types.append(rs.getString("type")).append(": ").append(rs.getInt("count")).append(", ");
            }
            feedback.feedbackTypes = types.length() > 0 ? types.substring(0, types.length() - 2) : "None";

            // Recurring Issues
            stmt = conn.prepareStatement(
                    "SELECT comment, COUNT(*) as count FROM feedback " +
                            "WHERE rating <= 2 GROUP BY comment ORDER BY count DESC LIMIT 5"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                feedback.recurringIssues.add(rs.getString("comment"));
            }
            if (feedback.recurringIssues.isEmpty()) feedback.recurringIssues.add("None");

            // Unresolved Feedback
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM feedback WHERE status = 'OPEN'"
            );
            rs = stmt.executeQuery();
            if (rs.next()) feedback.unresolvedFeedback = rs.getInt(1);
        }
        return feedback;
    }

    public static class ProgramParticipation {
        public int totalPrograms;
        public List<String> popularPrograms;
        public int upcomingPrograms;
        public double engagementRate;
    }

    public ProgramParticipation getProgramParticipation() throws SQLException {
        ProgramParticipation programs = new ProgramParticipation();
        programs.popularPrograms = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Total Programs
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT program_name) FROM loyalty_programs"
            );
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) programs.totalPrograms = rs.getInt(1);

            // Popular Programs
            stmt = conn.prepareStatement(
                    "SELECT program_name, COUNT(*) as enrollments " +
                            "FROM loyalty_programs GROUP BY program_name ORDER BY enrollments DESC LIMIT 5"
            );
            rs = stmt.executeQuery();
            while (rs.next()) {
                programs.popularPrograms.add(rs.getString("program_name"));
            }
            if (programs.popularPrograms.isEmpty()) programs.popularPrograms.add("None");

            // Upcoming/Ending Programs
            stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT program_name) FROM loyalty_programs " +
                            "WHERE start_date > CURDATE() OR end_date < DATE_ADD(CURDATE(), INTERVAL 1 MONTH)"
            );
            rs = stmt.executeQuery();
            if (rs.next()) programs.upcomingPrograms = rs.getInt(1);

            // Engagement Rate
            stmt = conn.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM loyalty_programs) / (SELECT COUNT(*) FROM users) * 100 as rate"
            );
            rs = stmt.executeQuery();
            programs.engagementRate = rs.next() ? rs.getDouble("rate") : 0.0;
        }
        return programs;
    }

    public static class SystemHealth {
        public int failedBookings;
        public int overbookings;
        public int dataIssues;
        public int lowAvailability;
    }

    public SystemHealth getSystemHealth() throws SQLException {
        SystemHealth health = new SystemHealth();
        try (Connection conn = getConnection()) {
            // Failed Bookings
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bookings WHERE status = 'FAILED'"
            );
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) health.failedBookings = rs.getInt(1);

            // Data Issues (e.g., null critical fields)
            stmt = conn.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM bookings WHERE offer_id IS NULL OR cost IS NULL) + " +
                            "(SELECT COUNT(*) FROM offers WHERE type IS NULL OR price IS NULL) as issues"
            );
            rs = stmt.executeQuery();
            if (rs.next()) health.dataIssues = rs.getInt("issues");

            // Low Availability (e.g., <10% capacity remaining)
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM offers o " +
                            "WHERE o.capacity > 0 AND " +
                            "(SELECT SUM(b.passengers) FROM bookings b WHERE b.offer_id = o.offer_id AND b.status = 'CONFIRMED') / o.capacity > 0.9"
            );
            rs = stmt.executeQuery();
            if (rs.next()) health.lowAvailability = rs.getInt(1);
        }
        return health;
    }
}