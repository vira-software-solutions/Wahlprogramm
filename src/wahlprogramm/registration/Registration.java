package registration;

import com.sun.glass.ui.Application;
import database.DatabaseManager;
import database.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import login.Login;
import main.MainController;
import main.Toast;

import java.sql.SQLException;

public class Registration extends VBox {
    @FXML
    private VBox root;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password2;

    @FXML
    private PasswordField password21;

    @FXML
    void OnCanceled(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onAcceptBtnClicked(ActionEvent event) throws SQLException {
        if(username.getText().isEmpty()){
            Toast.makeText(MainController.getMainStage(),
                    "Der Benutzername muss gesetzt werden!",
                    3500,
                    500,
                    500);
            return;
        }

        if(password2.getText().equals(password21.getText())){
            DatabaseManager.initializeDatabase(new User(username.getText(), password2.getText()));
            MainController.setContent(MainController.loadFXML(Login.class.getResource("/login/login.fxml")));
        }
        else{
            Toast.makeText(MainController.getMainStage(),
                    "Die Passwörter stimmen nicht überein!",
                    3500,
                    500,
                    500);
        }
    }
}
