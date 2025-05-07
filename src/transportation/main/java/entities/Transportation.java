package entities;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Transportation {
    private int id;
    private String type;  // "bus", "train", "taxi", "rental_car"
    private String providerName;
    private String departurePoint;
    private String arrivalPoint;
    private double departureLat;
    private double departureLng;
    private double arrivalLat;
    private double arrivalLng;
    private LocalTime departureTime;
    private int durationMinutes;
    private double price;
    private String operatingDays; // "1111100" for MTWTFSS

    public Transportation(String type, String providerName, String departurePoint, String arrivalPoint,
                          double departureLat, double departureLng, double arrivalLat, double arrivalLng,
                          LocalTime departureTime, int durationMinutes, double price, String operatingDays) {
        this.type = type;
        this.providerName = providerName;
        this.departurePoint = departurePoint;
        this.arrivalPoint = arrivalPoint;
        this.departureLat = departureLat;
        this.departureLng = departureLng;
        this.arrivalLat = arrivalLat;
        this.arrivalLng = arrivalLng;
        this.departureTime = departureTime;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.operatingDays = operatingDays;
    }

    public Transportation(int id, String type, String providerName, String departurePoint, String arrivalPoint,
                          double departureLat, double departureLng, double arrivalLat, double arrivalLng,
                          LocalTime departureTime, int durationMinutes, double price, String operatingDays,
                          String description, String photoUrl) {
        this(type, providerName, departurePoint, arrivalPoint, departureLat, departureLng,
                arrivalLat, arrivalLng, departureTime, durationMinutes, price, operatingDays);
        this.id = id;
    }

    public Transportation(int id, String type, String providerName, String departurePoint, String arrivalPoint, double departureLat, double departureLng, double arrivalLat, double arrivalLng, LocalTime localDepartureTime, int durationMinutes, double price, String operatingDays) {
        this.id = id;
        this.type = type;
        this.providerName = providerName;
        this.departurePoint = departurePoint;
        this.arrivalPoint = arrivalPoint;
        this.departureLat = departureLat;
        this.departureLng = departureLng;
        this.arrivalLat = arrivalLat;
        this.arrivalLng = arrivalLng;
        this.departureTime = localDepartureTime;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.operatingDays = operatingDays;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getDeparturePoint() { return departurePoint; }
    public void setDeparturePoint(String departurePoint) { this.departurePoint = departurePoint; }

    public String getArrivalPoint() { return arrivalPoint; }
    public void setArrivalPoint(String arrivalPoint) { this.arrivalPoint = arrivalPoint; }

    public double getDepartureLat() { return departureLat; }
    public void setDepartureLat(double departureLat) { this.departureLat = departureLat; }

    public double getDepartureLng() { return departureLng; }
    public void setDepartureLng(double departureLng) { this.departureLng = departureLng; }

    public double getArrivalLat() { return arrivalLat; }
    public void setArrivalLat(double arrivalLat) { this.arrivalLat = arrivalLat; }

    public double getArrivalLng() { return arrivalLng; }
    public void setArrivalLng(double arrivalLng) { this.arrivalLng = arrivalLng; }

    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getOperatingDays() { return operatingDays; }
    public void setOperatingDays(String operatingDays) { this.operatingDays = operatingDays; }

    // Helper methods
    public LocalTime getArrivalTime() {
        return departureTime.plusMinutes(durationMinutes);
    }

    public boolean operatesOnDay(DayOfWeek day) {
        int dayIndex = day.getValue() - 1; // Monday=0 to Sunday=6
        return operatingDays.charAt(dayIndex) == '1';
    }

    @Override
    public String toString() {
        return "Transportation{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", providerName='" + providerName + '\'' +
                ", departurePoint='" + departurePoint + '\'' +
                ", arrivalPoint='" + arrivalPoint + '\'' +
                ", departureTime=" + departureTime +
                ", durationMinutes=" + durationMinutes +
                ", price=" + price +
                '}';
    }
}