package entities;

public class TransportationPlus {
    private int id;
    private int transportId;
    private int delaysPerMonth;
    private int trafficTime;
    private int passengersSumPerDay;

    public TransportationPlus(int transportId, int delaysPerMonth, int trafficTime, int passengersSumPerDay) {
        this.transportId = transportId;
        this.delaysPerMonth = delaysPerMonth;
        this.trafficTime = trafficTime;
        this.passengersSumPerDay = passengersSumPerDay;
    }

    public TransportationPlus(int id, int transportId, int delaysPerMonth, int trafficTime, int passengersSumPerDay) {
        this.id = id;
        this.transportId = transportId;
        this.delaysPerMonth = delaysPerMonth;
        this.trafficTime = trafficTime;
        this.passengersSumPerDay = passengersSumPerDay;
    }

    public TransportationPlus() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransportId() { return transportId; }
    public void setTransportId(int transportId) { this.transportId = transportId; }

    public int getDelaysPerMonth() { return delaysPerMonth; }
    public void setDelaysPerMonth(int delaysPerMonth) { this.delaysPerMonth = delaysPerMonth; }

    public int getTrafficTime() { return trafficTime; }
    public void setTrafficTime(int trafficTime) { this.trafficTime = trafficTime; }

    public int getPassengersSumPerDay() { return passengersSumPerDay; }
    public void setPassengersSumPerDay(int passengersSumPerDay) { this.passengersSumPerDay = passengersSumPerDay; }

    // Helper method: Calculate reliability score (example)
    public double calculateReliabilityScore() {
        // A simple reliability score: lower delays and traffic time increase reliability
        // Normalize delays (assuming max 30 delays per month) and traffic time (assuming max 60 minutes)
        double delayScore = 1.0 - (delaysPerMonth / 30.0); // More delays -> lower score
        double trafficScore = 1.0 - (trafficTime / 60.0);  // More traffic time -> lower score
        return (delayScore * 0.6 + trafficScore * 0.4) * 100; // Weighted score out of 100
    }
}
