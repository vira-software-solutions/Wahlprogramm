/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends VBox implements Initializable {
    @FXML
    private VBox root;

    private static VBox mainPane;

    public static VBox getMainPane(){
        return mainPane;
    }

    public static Stage getMainStage(){
        return (Stage) mainPane.getScene().getWindow();
    }

    public static void setContent(Node node) {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(node);
    }

    public static Node loadFXML(URL url) {
        try {
            return new FXMLLoader(url).load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane = root;
    }
}
