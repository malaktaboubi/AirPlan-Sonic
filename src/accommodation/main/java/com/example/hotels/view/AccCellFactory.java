package com.example.hotels.view;

import com.example.hotels.controllers.AccCellController;
import com.example.hotels.models.Hebergement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class AccCellFactory extends ListCell<Hebergement> {

    @Override
    protected void updateItem(Hebergement hebergement, boolean empty) {
        super.updateItem(hebergement, empty);

        if (empty || hebergement == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/hotels/fxml/AccCell_agency.fxml"));
                Node cellView = loader.load();
                AccCellController controller = loader.getController();
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



