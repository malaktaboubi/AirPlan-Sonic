package controllers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;


public class FxmlUtils {
        public static Node loadFXML(String fxmlPath) throws IOException {
            return FXMLLoader.load(FxmlUtils.class.getResource(fxmlPath));
        }

}

