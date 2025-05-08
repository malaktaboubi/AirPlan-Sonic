package services;

import entities.Transportation;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransportation {
    private static Connection cnx;

    public ServiceTransportation() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    public void ajouter(Transportation h) {
        String req = "INSERT INTO transportation (type, provider_name, departure_point, arrival_point, " +
                "departure_Lat, departure_Lng, arrival_Lat, arrival_Lng, " +
                "departure_time, duration_minutes, price, operating_days) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, h.getType());
            ps.setString(2, h.getProviderName());
            ps.setString(3, h.getDeparturePoint());
            ps.setString(4, h.getArrivalPoint());
            ps.setDouble(5, h.getDepartureLat());
            ps.setDouble(6, h.getDepartureLng());
            ps.setDouble(7, h.getArrivalLat());
            ps.setDouble(8, h.getArrivalLng());
            ps.setTime(9, Time.valueOf(h.getDepartureTime()));
            ps.setInt(10, h.getDurationMinutes());
            ps.setDouble(11, h.getPrice());
            ps.setString(12, h.getOperatingDays());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        h.setId(generatedKeys.getInt(1));
                        System.out.println("Transportation added with ID: " + h.getId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding transportation: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    public void modifier(Transportation h) {
        String req = "UPDATE transportation SET type=?, provider_name=?, departure_point=?, arrival_point=?, "
                + "departure_Lat=?, departure_Lng=?, arrival_Lat=?, arrival_Lng=?, "
                + "departure_time=?, duration_minutes=?, price=?, operating_days=? "
                + "WHERE id_transport=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, h.getType());
            ps.setString(2, h.getProviderName());
            ps.setString(3, h.getDeparturePoint());
            ps.setString(4, h.getArrivalPoint());
            ps.setDouble(5, h.getDepartureLat());
            ps.setDouble(6, h.getDepartureLng());
            ps.setDouble(7, h.getArrivalLat());
            ps.setDouble(8, h.getArrivalLng());
            ps.setTime(9, Time.valueOf(h.getDepartureTime()));
            ps.setInt(10, h.getDurationMinutes());
            ps.setDouble(11, h.getPrice());
            ps.setString(12, h.getOperatingDays());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Transportation updated successfully");
            } else {
                System.out.println("No transportation found with ID: " + h.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating transportation: " + e.getMessage());
            throw new RuntimeException("Database error during update", e);
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM transportation WHERE id_transport=id";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Transportation deleted!");
        } catch (SQLException e) {
            System.err.println("Error deleting transportation: " + e.getMessage());
        }
    }

    public static List<Transportation> afficher() {
        List<Transportation> transports = new ArrayList<>();

        String query = "SELECT * FROM transportation";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                try {
                    Transportation transport = mapResultSetToTransportation(rs);
                    transports.add(transport);
                } catch (SQLException e) {
                    System.err.println("Error mapping transportation record: " + e.getMessage());
                }
            }

            System.out.println(transports.size() + " transports retrieved successfully");
            return transports;

        } catch (SQLException e) {
            System.err.println("Error retrieving transports: " + e.getMessage());
            throw new RuntimeException("Database access error", e);
        }
    }

    public List<Transportation> findByDepartureAndArrival(String departure, String arrival) {
        List<Transportation> transports = new ArrayList<>();

        String query = "SELECT * FROM transportation WHERE departure_point LIKE ? AND arrival_point LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, "%" + departure + "%");
            ps.setString(2, "%" + arrival + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transportation transport = mapResultSetToTransportation(rs);
                    transports.add(transport);
                }
            }

            System.out.println(transports.size() + " transports found for " + departure + " to " + arrival);
            return transports;

        } catch (SQLException e) {
            System.err.println("Error finding transports: " + e.getMessage());
            throw new RuntimeException("Database query error", e);
        }
    }

    private static Transportation mapResultSetToTransportation(ResultSet rs) throws SQLException {
        Time departureTime = rs.getTime("departure_time");
        LocalTime localDepartureTime = departureTime != null ? departureTime.toLocalTime() : null;

        return new Transportation(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getString("provider_name"),
                rs.getString("departure_point"),
                rs.getString("arrival_point"),
                rs.getDouble("departure_lat"),
                rs.getDouble("departure_lng"),
                rs.getDouble("arrival_lat"),
                rs.getDouble("arrival_lng"),
                localDepartureTime,
                rs.getInt("duration_minutes"),
                rs.getDouble("price"),
                rs.getString("operating_days")
        );
    }
}