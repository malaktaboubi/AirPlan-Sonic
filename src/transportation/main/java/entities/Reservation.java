package entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private int idReservation;
    private int userId;
    private int transportId;
    private String departure_point;
    private String arrival_point;
    private LocalDate travelDate;
    private int passengers;
    private LocalDateTime bookingTime;
    private String status; // 'pending', 'confirmed', 'cancelled'

    public Reservation(int userId, int transportId, String departure_point , String arrival_point , LocalDate travelDate, int passengers, LocalDateTime bookingTime, String status) {
        this.userId = userId;
        this.transportId = transportId;
        this.departure_point = departure_point;
        this.arrival_point = arrival_point;
        this.travelDate = travelDate;
        this.passengers = passengers;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public Reservation(int idReservation, int userId, int transportId,String departure_point , String arrival_point, LocalDate travelDate, int passengers, LocalDateTime bookingTime, String status) {
        this.idReservation = idReservation;
        this.userId = userId;
        this.transportId = transportId;
        this.departure_point = departure_point;
        this.arrival_point = arrival_point;
        this.travelDate = travelDate;
        this.passengers = passengers;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    public Reservation() {
    }

    // Getters and Setters
    public int getId() { return idReservation; }
    public void setId(int idReservation) { this.idReservation = idReservation; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public String getDeparturePoint() { return departure_point; }
    public void setDeparturePoint(String departure_point) {
        this.departure_point = departure_point;
    }
    public String getArrivalPoint() { return arrival_point; }
    public void setArrivalPoint(String arrival_point) {
        this.arrival_point = arrival_point;
    }

    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }

    public int getPassengers() { return passengers; }
    public void setPassengers(int passengers) { this.passengers = passengers; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Helper method
    public LocalDateTime getEstimatedArrivalTime(Transportation transportation) {
        if (transportation != null && transportation.getDepartureTime() != null) {
            return LocalDateTime.of(travelDate, transportation.getDepartureTime())
                    .plusMinutes(transportation.getDurationMinutes());
        }
        return null;
    }

    // Validation method for status
    public boolean isValidStatus() {
        return status != null && (status.equals("pending") || status.equals("confirmed") || status.equals("cancelled"));
    }
}
