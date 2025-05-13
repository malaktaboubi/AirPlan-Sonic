package servicesAmineM;

import java.sql.*;
import java.util.*;

public class ServiceAgency {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/maalejdb";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    public static class BusinessOverview {
        public int totalOffers;
        public int activeOffers;
        public int expiredOffers;
        public String mostPopularOfferType;
        public int totalReservations;
    }

    public BusinessOverview getBusinessOverview(int agencyId) throws SQLException {
        BusinessOverview overview = new BusinessOverview();
        try (Connection conn = getConnection()) {
            // Total Offers
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM offers WHERE agency_id = ?");
            stmt.setInt(1, agencyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) overview.totalOffers = rs.getInt(1);

            // Active vs Expired
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM offers WHERE agency_id = ? AND end_date >= CURDATE()");
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            if (rs.next()) overview.activeOffers = rs.getInt(1);
            overview.expiredOffers = overview.totalOffers - overview.activeOffers;

            // Most Popular Offer Type
            stmt = conn.prepareStatement(
                    "SELECT o.type, COUNT(b.id) as bookings " +
                            "FROM offers o LEFT JOIN bookings b ON o.offer_id = b.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY o.type ORDER BY bookings DESC LIMIT 1"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            overview.mostPopularOfferType = rs.next() ? rs.getString("type") : "None";

            // Total Reservations
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id WHERE o.agency_id = ?"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            if (rs.next()) overview.totalReservations = rs.getInt(1);
        }
        return overview;
    }

    public static class RevenueInsights {
        public double totalIncome;
        public double averagePrice;
        public String mostProfitableService;
        public Map<String, Double> monthlyRevenue;
    }

    public RevenueInsights getRevenueInsights(int agencyId) throws SQLException {
        RevenueInsights revenue = new RevenueInsights();
        revenue.monthlyRevenue = new LinkedHashMap<>();
        try (Connection conn = getConnection()) {
            // Total Income
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT SUM(b.cost) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED'"
            );
            stmt.setInt(1, agencyId);
            ResultSet rs = stmt.executeQuery();
            revenue.totalIncome = rs.next() ? rs.getDouble(1) : 0.0;

            // Average Price
            stmt = conn.prepareStatement(
                    "SELECT AVG(b.cost) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED'"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            revenue.averagePrice = rs.next() ? rs.getDouble(1) : 0.0;

            // Most Profitable Service
            stmt = conn.prepareStatement(
                    "SELECT o.type, SUM(b.cost) as total " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED' " +
                            "GROUP BY o.type ORDER BY total DESC LIMIT 1"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            revenue.mostProfitableService = rs.next() ? rs.getString("type") : "None";

            // Monthly Revenue
            stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(b.booking_date, '%Y-%m') as month, SUM(b.cost) as total " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED' " +
                            "GROUP BY month ORDER BY month"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                revenue.monthlyRevenue.put(rs.getString("month"), rs.getDouble("total"));
            }
        }
        return revenue;
    }

    public static class OfferPerformance {
        public List<String> topOffers;
        public double occupancyRate;
        public int cancelledBookings;
        public int positiveFeedback;
        public int negativeFeedback;
    }

    public OfferPerformance getOfferPerformance(int agencyId) throws SQLException {
        OfferPerformance performance = new OfferPerformance();
        performance.topOffers = new ArrayList<>();
        try (Connection conn = getConnection()) {
            // Top 5 Offers
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT o.details, COUNT(b.id) as bookings " +
                            "FROM offers o LEFT JOIN bookings b ON o.offer_id = b.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY o.offer_id, o.details " +
                            "ORDER BY bookings DESC LIMIT 5"
            );
            stmt.setInt(1, agencyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                performance.topOffers.add(rs.getString("details"));
            }
            if (performance.topOffers.isEmpty()) performance.topOffers.add("None");

            // Occupancy Rate
            stmt = conn.prepareStatement(
                    "SELECT AVG(CAST(b.passengers AS DOUBLE) / o.capacity) * 100 as rate " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED' AND o.capacity > 0"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            performance.occupancyRate = rs.next() ? rs.getDouble("rate") : 0.0;

            // Cancelled Bookings
            stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CANCELLED'"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            if (rs.next()) performance.cancelledBookings = rs.getInt(1);

            // Feedback Polarity
            stmt = conn.prepareStatement(
                    "SELECT SUM(CASE WHEN f.rating >= 3 THEN 1 ELSE 0 END) as positive, " +
                            "SUM(CASE WHEN f.rating < 3 THEN 1 ELSE 0 END) as negative " +
                            "FROM feedback f JOIN offers o ON f.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ?"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                performance.positiveFeedback = rs.getInt("positive");
                performance.negativeFeedback = rs.getInt("negative");
            }
        }
        return performance;
    }

    public static class TimeBasedInsights {
        public String peakPeriods;
        public double averageLeadTime;
        public Map<String, Integer> bookingsPerMonth;
    }

    public TimeBasedInsights getTimeBasedInsights(int agencyId) throws SQLException {
        TimeBasedInsights insights = new TimeBasedInsights();
        insights.bookingsPerMonth = new LinkedHashMap<>();
        try (Connection conn = getConnection()) {
            // Peak Periods
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(b.booking_date, '%M') as month, COUNT(*) as bookings " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY month ORDER BY bookings DESC LIMIT 1"
            );
            stmt.setInt(1, agencyId);
            ResultSet rs = stmt.executeQuery();
            insights.peakPeriods = rs.next() ? rs.getString("month") : "None";

            // Average Lead Time
            stmt = conn.prepareStatement(
                    "SELECT AVG(DATEDIFF(b.booking_date, o.start_date)) as lead_time " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? AND b.status = 'CONFIRMED'"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            insights.averageLeadTime = rs.next() ? rs.getDouble("lead_time") : 0.0;

            // Bookings per Month
            stmt = conn.prepareStatement(
                    "SELECT DATE_FORMAT(b.booking_date, '%Y-%m') as month, COUNT(*) as bookings " +
                            "FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY month ORDER BY month"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                insights.bookingsPerMonth.put(rs.getString("month"), rs.getInt("bookings"));
            }
        }
        return insights;
    }

    public static class CustomerFeedback {
        public int totalFeedback;
        public String feedbackByType;
        public String commonFeedback;
        public double averageRating;
    }

    public CustomerFeedback getCustomerFeedback(int agencyId) throws SQLException {
        CustomerFeedback feedback = new CustomerFeedback();
        try (Connection conn = getConnection()) {
            // Total Feedback
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM feedback f JOIN offers o ON f.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ?"
            );
            stmt.setInt(1, agencyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) feedback.totalFeedback = rs.getInt(1);

            // Feedback by Type
            stmt = conn.prepareStatement(
                    "SELECT o.type, COUNT(f.id) as count " +
                            "FROM feedback f JOIN offers o ON f.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY o.type"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            StringBuilder feedbackByType = new StringBuilder();
            while (rs.next()) {
                feedbackByType.append(rs.getString("type")).append(": ").append(rs.getInt("count")).append(", ");
            }
            feedback.feedbackByType = feedbackByType.length() > 0 ? feedbackByType.substring(0, feedbackByType.length() - 2) : "None";

            // Common Feedback
            stmt = conn.prepareStatement(
                    "SELECT comment, COUNT(*) as count FROM feedback f JOIN offers o ON f.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ? GROUP BY comment ORDER BY count DESC LIMIT 1"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            feedback.commonFeedback = rs.next() ? rs.getString("comment") : "None";

            // Average Rating
            stmt = conn.prepareStatement(
                    "SELECT AVG(rating) FROM feedback f JOIN offers o ON f.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ?"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            feedback.averageRating = rs.next() ? rs.getDouble(1) : 0.0;
        }
        return feedback;
    }

    public static class MarketingEngagement {
        public double conversionRate;
        public int programsJoined;
        public double retentionRate;
    }

    public MarketingEngagement getMarketingEngagement(int agencyId) throws SQLException {
        MarketingEngagement engagement = new MarketingEngagement();
        try (Connection conn = getConnection()) {
            // Conversion Rate
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT (SELECT COUNT(*) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id WHERE o.agency_id = ?) / " +
                            "(SELECT COUNT(*) FROM offer_views v JOIN offers o ON v.offer_id = o.offer_id WHERE o.agency_id = ?) * 100 as rate"
            );
            stmt.setInt(1, agencyId);
            stmt.setInt(2, agencyId);
            ResultSet rs = stmt.executeQuery();
            engagement.conversionRate = rs.next() ? rs.getDouble("rate") : 0.0;

            // Programs Joined
            stmt = conn.prepareStatement(
                    "SELECT COUNT(DISTINCT lp.id) FROM loyalty_programs lp " +
                            "JOIN bookings b ON lp.user_id = b.user_id JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ?"
            );
            stmt.setInt(1, agencyId);
            rs = stmt.executeQuery();
            if (rs.next()) engagement.programsJoined = rs.getInt(1);

            // Retention Rate
            stmt = conn.prepareStatement(
                    "SELECT (SELECT COUNT(DISTINCT b1.user_id) FROM bookings b1 JOIN offers o1 ON b1.offer_id = o1.offer_id " +
                            "WHERE o1.agency_id = ? AND EXISTS (SELECT 1 FROM bookings b2 JOIN offers o2 ON b2.offer_id = o2.offer_id " +
                            "WHERE o2.agency_id = ? AND b2.user_id = b1.user_id AND b2.id != b1.id)) / " +
                            "(SELECT COUNT(DISTINCT b.user_id) FROM bookings b JOIN offers o ON b.offer_id = o.offer_id " +
                            "WHERE o.agency_id = ?) * 100 as rate"
            );
            stmt.setInt(1, agencyId);
            stmt.setInt(2, agencyId);
            stmt.setInt(3, agencyId);
            rs = stmt.executeQuery();
            engagement.retentionRate = rs.next() ? rs.getDouble("rate") : 0.0;
        }
        return engagement;
    }
}