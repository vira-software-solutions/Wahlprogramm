/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package login;

import database.DatabaseManager;
import database.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import main.CollectionOfCollections;
import main.MainController;
import tabs.Tabs;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Login implements Initializable {

    @FXML
    public Button confirmationButton;

    @FXML
    public TextField username;

    @FXML
    public PasswordField password;

    @FXML
    public ProgressBar progressBar;

    public void handleConfirmation() {
        progressBar.setVisible(true);
        confirmationButton.setDisable(true);

        new Thread(() -> {
            try {
                if (DatabaseManager.confirmUser(new
                        User(username.getText(),
                        password.getText()))) {
                    Platform.runLater(() -> MainController.setContent(
                            MainController.loadFXML(
                                    Tabs.class.getResource("/tabs/tabs.fxml")
                            )
                    ));
                } else {
                    username.setText("");
                    password.setText("");

                    if (!username.getStyleClass().contains("error")) {
                        username.getStyleClass().add("error");
                        password.getStyleClass().add("error");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            confirmationButton.setDisable(false);
            progressBar.setVisible(false);
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            CollectionOfCollections.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
