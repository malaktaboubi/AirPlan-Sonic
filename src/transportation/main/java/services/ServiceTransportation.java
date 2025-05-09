package services;

import entities.Transportation;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.*;
import java.util.Optional;


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
                + "WHERE id=?";

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
            ps.setInt(13, h.getId());

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
        String req = "DELETE FROM transportation WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Transportation deleted!");
        } catch (SQLException e) {
            System.err.println("Error deleting transportation: " + e.getMessage());
        }
    }

    private static Stream<ResultSet> toStream(ResultSet rs) {
        Iterable<ResultSet> iterable = () -> new Iterator<ResultSet>() {
            @Override
            public boolean hasNext() {
                try {
                    return !rs.isClosed() && !rs.isAfterLast() && rs.next();
                } catch (SQLException e) {
                    throw new RuntimeException("Error iterating through ResultSet", e);
                }
            }

            @Override
            public ResultSet next() {
                return rs;
            }
        };

        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static List<Transportation> afficher() {
        String query = "SELECT * FROM transportation";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Transportation> transports = new ArrayList<>();

            while (rs.next()) {
                mapResultSetSafely(rs).ifPresent(transports::add);
            }

            System.out.println(transports.size() + " transports retrieved successfully");
            return transports;

        } catch (SQLException e) {
            System.err.println("Error retrieving transports: " + e.getMessage());
            throw new RuntimeException("Database access error", e);
        }
    }

    private static Optional<Transportation> mapResultSetSafely(ResultSet rs) {
        try {
            return Optional.of(mapResultSetToTransportation(rs));
        } catch (SQLException e) {
            System.err.println("Error mapping transportation record: " + e.getMessage());
            return Optional.empty();
        }
    }


        private static Transportation mapResultSetToTransportation(ResultSet rs) throws SQLException {
            Transportation transport = new Transportation();
            transport.setId(rs.getInt("id_transport"));
            transport.setType(rs.getString("type"));
            transport.setProviderName(rs.getString("provider_name"));
            transport.setDeparturePoint(rs.getString("departure_point"));
            transport.setArrivalPoint(rs.getString("arrival_point"));
            transport.setDepartureLat(rs.getDouble("departure_lat"));
            transport.setDepartureLng(rs.getDouble("departure_lng"));
            transport.setArrivalLat(rs.getDouble("arrival_lat"));
            transport.setArrivalLng(rs.getDouble("arrival_lng"));
            transport.setDepartureTime(rs.getTime("departure_time").toLocalTime());
            transport.setDurationMinutes(rs.getInt("duration_minutes"));
            transport.setPrice(rs.getDouble("price"));
            transport.setOperatingDays(rs.getString("operating_days"));
            transport.setPhoto(rs.getString("photo"));

            // etc.
            return transport;
        }


}