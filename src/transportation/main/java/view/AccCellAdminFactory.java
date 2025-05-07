package view;

import controllersEya.AccCellAdmin;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import modelsEya.Hebergement;

import java.io.IOException;

public class AccCellAdminFactory extends ListCell<Hebergement> {

    @Override
    protected void updateItem(Hebergement hebergement, boolean empty) {
        super.updateItem(hebergement, empty);

        if (empty || hebergement == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotels/fxml/AccCell_admin.fxml"));
                Node cellView = loader.load();
                AccCellAdmin controller = loader.getController();
                controller.setHebergement(hebergement);

                // Supprimer cette ligne si ton FXML contient du texte
                setText(null);
                setGraphic(cellView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}



