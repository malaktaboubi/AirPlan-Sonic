package entities;

import java.time.LocalDate;

public class TransportationPlusHistory {
    private int id;
    private int transportId;
    private LocalDate recordedDate;
    private int delaysPerMonth;
    private int trafficTime;
    private int passengersSumPerDay;

    public TransportationPlusHistory(int transportId, LocalDate recordedDate, int delaysPerMonth, int trafficTime, int passengersSumPerDay) {
        this.transportId = transportId;
        this.recordedDate = recordedDate;
        this.delaysPerMonth = delaysPerMonth;
        this.trafficTime = trafficTime;
        this.passengersSumPerDay = passengersSumPerDay;
    }

    public TransportationPlusHistory(int id, int transportId, LocalDate recordedDate, int delaysPerMonth, int trafficTime, int passengersSumPerDay) {
        this.id = id;
        this.transportId = transportId;
        this.recordedDate = recordedDate;
        this.delaysPerMonth = delaysPerMonth;
        this.trafficTime = trafficTime;
        this.passengersSumPerDay = passengersSumPerDay;
    }

    public TransportationPlusHistory() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public LocalDate getRecordedDate() { return recordedDate; }
    public void setRecordedDate(LocalDate recordedDate) { this.recordedDate = recordedDate; }

    public int getDelaysPerMonth() { return delaysPerMonth; }
    public void setDelaysPerMonth(int delaysPerMonth) { this.delaysPerMonth = delaysPerMonth; }

    public int getTrafficTime() { return trafficTime; }
    public void setTrafficTime(int trafficTime) { this.trafficTime = trafficTime; }

    public int getPassengersSumPerDay() { return passengersSumPerDay; }
    public void setPassengersSumPerDay(int passengersSumPerDay) { this.passengersSumPerDay = passengersSumPerDay; }
}
