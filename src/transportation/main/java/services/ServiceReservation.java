package services;

import entities.Reservation;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceReservation implements IServiceTransport<Reservation> {
    private static Connection cnx;

    public ServiceReservation() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reservation reservation) {
        String req = "INSERT INTO reservation (user_id, id_transport, departure_point, arrival_point, travel_date, passengers, booking_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getTransportId());
            ps.setString(3, reservation.getDeparturePoint());
            ps.setString(4, reservation.getArrivalPoint());
            ps.setDate(5, Date.valueOf(reservation.getTravelDate()));
            ps.setInt(6, reservation.getPassengers());
            ps.setTimestamp(7, Timestamp.valueOf(reservation.getBookingTime()));
            ps.setString(8, reservation.getStatus());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setUserId(generatedKeys.getInt(1));
                        System.out.println("Reservation added with ID: " + reservation.getUserId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public void modifier(Reservation reservation) {
        String req = "UPDATE reservation SET user_id=?, id_transport=? , departure_point=?, arrival_point=?, travel_date=?, passengers=?, booking_time=?, status=? " +
                "WHERE id_reservation_transport=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getTransportId());
            ps.setString(3, reservation.getDeparturePoint());
            ps.setString(4, reservation.getArrivalPoint());
            ps.setDate(5, Date.valueOf(reservation.getTravelDate()));
            ps.setInt(6, reservation.getPassengers());
            ps.setTimestamp(7, Timestamp.valueOf(reservation.getBookingTime()));
            ps.setString(8, reservation.getStatus());
            ps.setInt(9, reservation.getId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Reservation updated successfully");
            } else {
                System.out.println("No reservation found with ID: " + reservation.getUserId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            throw new RuntimeException("Database error during update", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM reservation WHERE id_reservation_transport=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Reservation deleted!");
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
    }

    public List<Reservation> afficher() {
        String query = "SELECT * FROM reservation";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Reservation> reservations = new ArrayList<>();

            while (rs.next()) {
                mapResultSetSafely(rs).ifPresent(reservations::add);
            }

            System.out.println(reservations.size() + " reservations retrieved successfully");
            return reservations;

        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
            throw new RuntimeException("Database access error", e);
        }
    }

    private Optional<Reservation> mapResultSetSafely(ResultSet rs) {
        try {
            return Optional.of(mapResultSetToReservation(rs));
        } catch (SQLException e) {
            System.err.println("Error mapping reservation record: " + e.getMessage());
            return Optional.empty();
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id_reservation_transport"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setTransportId(rs.getInt("id_transport"));
        reservation.setDeparturePoint(rs.getString("departure_point")); // New field
        reservation.setArrivalPoint(rs.getString("arrival_point")); // New field
        reservation.setTravelDate(rs.getDate("travel_date").toLocalDate());
        reservation.setPassengers(rs.getInt("passengers"));
        reservation.setBookingTime(rs.getTimestamp("booking_time").toLocalDateTime());
        reservation.setStatus(rs.getString("status"));
        return reservation;
    }


    public int getReservationTotal() {
        String query = "SELECT COUNT(*) as total FROM reservation";

        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return -1;
        }
    }

    public int canceledResCount() {
        String query = "SELECT COUNT(*) as count FROM reservation WHERE status='cancelled'";

        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return -1;
        }
    }


    public int pendingResCount() {
        String query = "SELECT COUNT(*) as count FROM reservation WHERE status='pending'";

        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return -1;
        }
    }



}