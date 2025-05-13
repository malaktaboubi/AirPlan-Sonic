package services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceCharts {
    private static Connection cnx;

    public ServiceCharts() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    public ObservableList<PieChart.Data> getTransportTypeStats() {
        ObservableList<PieChart.Data> dataList = FXCollections.observableArrayList();

        String query = "SELECT type, COUNT(*) AS count FROM transportation GROUP BY type";

        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                dataList.add(new PieChart.Data(type, count));
            }

        } catch (SQLException e) {
            System.err.println("Error loading transport data: " + e.getMessage());
        }

        return dataList;
    }

}
