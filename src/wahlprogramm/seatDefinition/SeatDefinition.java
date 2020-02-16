package seatDefinition;

import database.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tabs.election.SektionDataModel;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SeatDefinition extends VBox implements Initializable {
    @FXML
    public Spinner<Integer> districtParliamentSeatCountField;

    @FXML
    public Spinner<Integer> districtConferenceSeatCountField;

    @FXML
    private Button okBtn;

    @FXML
    private Button cancelBtn;

    private int originalValueBezR, originalValueBezK;

    private SektionDataModel currentSektionDataModel;

    public void initData(SektionDataModel sektionDataModel) {
        this.districtConferenceSeatCountField.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        Integer.MAX_VALUE,
                        sektionDataModel.getBezK()));

        this.districtParliamentSeatCountField.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        Integer.MAX_VALUE,
                        sektionDataModel.getBezR()));

        sektionDataModel.bezKProperty().bind(this.districtConferenceSeatCountField.valueProperty());
        sektionDataModel.bezRProperty().bind(this.districtParliamentSeatCountField.valueProperty());

        originalValueBezK = sektionDataModel.getBezK();
        originalValueBezR = sektionDataModel.getBezR();

        currentSektionDataModel = sektionDataModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void onOk(ActionEvent actionEvent) throws SQLException {
        DatabaseManager.updateSeatInformation(currentSektionDataModel);

        close();
    }

    public void onCancel(ActionEvent actionEvent) {
        close();

        this.districtConferenceSeatCountField.getValueFactory().setValue(originalValueBezK);
        this.districtParliamentSeatCountField.getValueFactory().setValue(originalValueBezR);
    }

    private void close() {
        Stage stage = (Stage) this.okBtn.getScene().getWindow();
        stage.close();
    }
}
