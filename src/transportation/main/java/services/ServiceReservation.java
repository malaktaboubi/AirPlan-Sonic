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
        String req = "INSERT INTO reservation_transport (user_id, transport_id, travel_date, passengers, booking_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getTransportId());
            ps.setDate(3, Date.valueOf(reservation.getTravelDate()));
            ps.setInt(4, reservation.getPassengers());
            ps.setTimestamp(5, Timestamp.valueOf(reservation.getBookingTime()));
            ps.setString(6, reservation.getStatus());

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
        String req = "UPDATE reservation_transport SET user_id=?, transport_id=?, travel_date=?, passengers=?, booking_time=?, status=? " +
                "WHERE id_reservation_transport=?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getTransportId());
            ps.setDate(3, Date.valueOf(reservation.getTravelDate()));
            ps.setInt(4, reservation.getPassengers());
            ps.setTimestamp(5, Timestamp.valueOf(reservation.getBookingTime()));
            ps.setString(6, reservation.getStatus());
            ps.setInt(7, reservation.getUserId());

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
        String req = "DELETE FROM reservation_transport WHERE id_reservation_transport=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Reservation deleted!");
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
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



}