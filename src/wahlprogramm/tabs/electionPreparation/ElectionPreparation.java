/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.electionPreparation;

import database.CandidatesDataModel;
import database.DatabaseManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import main.CollectionOfCollections;
import main.MainController;
import main.PropsManager;
import seatDefinition.SeatDefinition;
import seatDefinition.WindowListener;
import tabs.election.SektionDataModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ElectionPreparation extends VBox {
    @FXML
    public Button defineSeatCountBtn;

    @FXML
    private TableView<CandidatesDataModel> tableData;

    @FXML
    private TableColumn<CandidatesDataModel, CandidatesDataModel> delete;

    @FXML
    private TableColumn<CandidatesDataModel, String> name;

    @FXML
    private TableColumn<CandidatesDataModel, String> gender;

    @FXML
    private ComboBox<SektionDataModel> sektionComboBox;

    @FXML
    private ComboBox<String> funktionComboBox;

    @FXML
    private Button newCandidateBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private Button printBtn;

    @FXML
    private void initialize(){
        funktionComboBox.setItems(CollectionOfCollections.getRoles());
        sektionComboBox.setItems(CollectionOfCollections.getSektionDataModels());

        initTable();

        this.funktionComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            try {
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.sektionComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            try {
                loadData();
                defineSeatCountBtn.setDisable(false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        SeatDefinition.addOkListener((districtParliamentSeatCount, districtConferenceSeatCount) -> {
            sektionComboBox.getSelectionModel().getSelectedItem().setDistrictConferenceSeats(districtConferenceSeatCount);
            sektionComboBox.getSelectionModel().getSelectedItem().setDistrictParliamentSeats(districtParliamentSeatCount);
        });
    }

    private void initTable() {
        initCols();
    }

    private void initCols() {
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        gender.setCellValueFactory(new PropertyValueFactory<>("Gender"));
        delete.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );

        editableCols();
    }

    private void editableCols() {
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue()));

        delete.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Entfernen");

            @Override
            protected void updateItem(CandidatesDataModel candidatesDataModel, boolean empty) {
                super.updateItem(candidatesDataModel, empty);

                if (candidatesDataModel == null) {
                    setGraphic(null);
                    return;
                }

                setGraphic(deleteButton);
                deleteButton.setOnAction(
                        event -> getTableView().getItems().remove(candidatesDataModel)
                );
            }
        });
    }

    private void loadData() throws SQLException {
        if (sektionComboBox.getSelectionModel().getSelectedItem() == null ||
                funktionComboBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        tableData.setItems(FXCollections.observableArrayList(
                DatabaseManager.getCandidatesForRole(
                        sektionComboBox.getSelectionModel().getSelectedItem().getNum(),
                        funktionComboBox.getSelectionModel().getSelectedItem()
                )));


        try {
            gender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
            var allGenders = CollectionOfCollections.getGender();
            var blacklist = DatabaseManager.getBlacklistedGendersForRole(
                    this.funktionComboBox.getSelectionModel().getSelectedItem()
            );
            allGenders.removeAll(blacklist);

            gender.setCellFactory(ComboBoxTableCell.forTableColumn(allGenders));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateData() throws SQLException {
        for (CandidatesDataModel candidatesDataModel : tableData.getItems()) {
            if (DatabaseManager.doesCandidateAlreadyExist(candidatesDataModel)) {
                continue;
            }

            DatabaseManager.insertNewCandidate(candidatesDataModel);
        }

        DatabaseManager.insertCandidateForRoleForSektion(
                tableData.getItems(),
                funktionComboBox.getSelectionModel().getSelectedItem(),
                sektionComboBox.getSelectionModel().getSelectedItem().getNum());
    }

    @FXML
    private void onAddNewCandidate() {
        if ((sektionComboBox.getSelectionModel().getSelectedItem() == null) ||
                (funktionComboBox.getSelectionModel().getSelectedItem() == null)) {
            return;
        }

        tableData.getItems().add(
                new CandidatesDataModel(
                        "Neu",
                        ""
                )
        );
    }

    @FXML
    private void onUpdate() throws SQLException {
        DatabaseManager.dumpCandidatesForRoleOfSektion(
                sektionComboBox.getSelectionModel().getSelectedItem().getNum(),
                funktionComboBox.getSelectionModel().getSelectedItem());
        updateData();
        DatabaseManager.dumpUnusedCandidates();
    }

    @FXML
    private void onPrint(){
        // TODO: PRINT ELECTION
    }

    @FXML
    public void onDefineSeatCount(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainController.class.getResource("/seatDefinition/seatDefinition.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Define Seats");
            stage.setResizable(false);
            stage.setScene(scene);
            new JMetro(scene, Style.valueOf(PropsManager.Props.getProperty("app.style").toUpperCase()));
            stage.show();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}
