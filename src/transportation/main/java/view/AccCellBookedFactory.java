package view;

import controllersEya.BookedCellController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import modelsEya.Reservation;

import java.io.IOException;

public class AccCellBookedFactory implements Callback<ListView<Reservation>, ListCell<Reservation>> {

    private Runnable refreshCallback;

    public AccCellBookedFactory(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @Override
    public ListCell<Reservation> call(ListView<Reservation> param) {
        return new ListCell<Reservation>() {
            private FXMLLoader loader;

            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);

                if (empty || reservation == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    try {
                        if (loader == null) {
                            loader = new FXMLLoader(getClass().getResource("/com/example/hotels/fxml/BookedCell2.fxml"));
                            try {
                                setGraphic(loader.load());
                            } catch (IOException e) {
                                e.printStackTrace();
                                setText("FXML Error: " + e.getMessage());
                                setStyle("-fx-text-fill: red;");
                                return;
                            }
                        }

                        BookedCellController controller = loader.getController();
                        controller.setReservation(reservation);
                        controller.setRefreshCallback(refreshCallback);
                        setStyle("-fx-padding: 0 0 10 0; -fx-background-color: transparent;");

                    } catch (Exception e) {
                        e.printStackTrace();
                        setText("Error: " + e.getMessage());
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        };
    }
}