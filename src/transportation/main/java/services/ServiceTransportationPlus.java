package services;

import entities.TransportationPlus;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceTransportationPlus implements IServiceTransport<TransportationPlus> {
    private Connection cnx;

    public ServiceTransportationPlus() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(TransportationPlus transportationPlus) {
        String req = "INSERT INTO transportation_plus (transport_id, delays_per_month, traffic_time, passengers_sum_per_day) " +
                "VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, transportationPlus.getTransportId());
            ps.setInt(2, transportationPlus.getDelaysPerMonth());
            ps.setInt(3, transportationPlus.getTrafficTime());
            ps.setInt(4, transportationPlus.getPassengersSumPerDay());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transportationPlus.setId(generatedKeys.getInt(1));
                        System.out.println("TransportationPlus added with ID: " + transportationPlus.getId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding TransportationPlus: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public void modifier(TransportationPlus transportationPlus) {
        String req = "UPDATE transportation_plus SET transport_id=?, delays_per_month=?, " +
                "traffic_time=?, passengers_sum_per_day=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, transportationPlus.getTransportId());
            ps.setInt(2, transportationPlus.getDelaysPerMonth());
            ps.setInt(3, transportationPlus.getTrafficTime());
            ps.setInt(4, transportationPlus.getPassengersSumPerDay());
            ps.setInt(5, transportationPlus.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("TransportationPlus updated successfully");
            } else {
                System.out.println("No TransportationPlus found with ID: " + transportationPlus.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating TransportationPlus: " + e.getMessage());
            throw new RuntimeException("Database error during update", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM transportation_plus WHERE id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("TransportationPlus with ID " + id + " deleted successfully");
            } else {
                System.out.println("No TransportationPlus found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting TransportationPlus: " + e.getMessage());
            throw new RuntimeException("Database error during deletion", e);
        }
    }

    // Additional method to retrieve all TransportationPlus records
    public List<TransportationPlus> afficher() {
        String query = "SELECT * FROM transportation_plus";
        List<TransportationPlus> transports = new ArrayList<>();

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                mapResultSetSafely(rs).ifPresent(transports::add);
            }

            System.out.println(transports.size() + " TransportationPlus records retrieved");
            return transports;

        } catch (SQLException e) {
            System.err.println("Error retrieving TransportationPlus records: " + e.getMessage());
            throw new RuntimeException("Database access error", e);
        }
    }

    // Method to get a single TransportationPlus by ID
    public Optional<TransportationPlus> getById(int id) {
        String query = "SELECT * FROM transportation_plus WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTransportationPlus(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error retrieving TransportationPlus: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    private Optional<TransportationPlus> mapResultSetSafely(ResultSet rs) {
        try {
            return Optional.of(mapResultSetToTransportationPlus(rs));
        } catch (SQLException e) {
            System.err.println("Error mapping TransportationPlus record: " + e.getMessage());
            return Optional.empty();
        }
    }

    private TransportationPlus mapResultSetToTransportationPlus(ResultSet rs) throws SQLException {
        return new TransportationPlus(
                rs.getInt("id"),
                rs.getInt("transport_id"),
                rs.getInt("delays_per_month"),
                rs.getInt("traffic_time"),
                rs.getInt("passengers_sum_per_day")
        );
    }

    // Method to calculate average reliability score
    public double calculateAverageReliability() {
        String query = "SELECT AVG(1.0 - (delays_per_month/30.0 * 0.6 + traffic_time/60.0 * 0.4)) * 100 " +
                "AS avg_reliability FROM transportation_plus";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getDouble("avg_reliability");
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Error calculating average reliability: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }
}