package services;

import entities.TransportationPlusHistory;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceTransportationPlusHistory {
    private static Connection cnx;

    public ServiceTransportationPlusHistory() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    public List<TransportationPlusHistory> getHistoryByTransportId(int transportId) {
        List<TransportationPlusHistory> historyList = new ArrayList<>();
        String query = "SELECT * FROM transportation_plus_history WHERE id_transport = ? ORDER BY recorded_date ASC";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, transportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TransportationPlusHistory history = new TransportationPlusHistory();
                    history.setId(rs.getInt("id"));
                    history.setTransportId(rs.getInt("id_transport"));
                    history.setRecordedDate(rs.getDate("recorded_date").toLocalDate());
                    history.setDelaysPerMonth(rs.getInt("delays_perMonth"));
                    history.setTrafficTime(rs.getInt("traffic_time"));
                    history.setPassengersSumPerDay(rs.getInt("passengers_sum_perDay"));
                    historyList.add(history);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving history: " + e.getMessage());
            throw new RuntimeException("Database access error", e);
        }
        return historyList;
    }
}