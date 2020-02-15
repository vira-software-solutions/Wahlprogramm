package seatDefinition;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SeatDefinition extends VBox implements Initializable {
    @FXML
    public TextField districtParliamentSeatCountField;

    @FXML
    public TextField districtConferenceSeatCountField;

    @FXML
    private Button okBtn;

    @FXML
    private Button cancelBtn;

    private static final List<WindowListener> listeners = new ArrayList<>();

    public static void addOkListener(WindowListener windowListener){
        listeners.add(windowListener);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // force the field to be numeric only
        districtParliamentSeatCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                districtParliamentSeatCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        districtConferenceSeatCountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                districtConferenceSeatCountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void onOk(ActionEvent actionEvent) {
        listeners.forEach(l->
                l.okButtonPressed(
                        Integer.parseInt(districtParliamentSeatCountField.getText()),
                        Integer.parseInt(districtConferenceSeatCountField.getText())));

        close();
    }

    public void onCancel(ActionEvent actionEvent) {
        close();
    }

    private void close(){
        Stage stage = (Stage) this.okBtn.getScene().getWindow();
        stage.close();
    }
}
