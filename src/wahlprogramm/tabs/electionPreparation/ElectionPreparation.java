/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.electionPreparation;

import database.DatabaseManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class ElectionPreparation extends VBox {
    @FXML
    private TableView<CandidatesDataModel> tableData;

    @FXML
    private TableColumn<CandidatesDataModel, CandidatesDataModel> delete;

    @FXML
    private TableColumn<CandidatesDataModel, String> name;

    @FXML
    private TableColumn<CandidatesDataModel, String> gender;

    @FXML
    private ComboBox<Integer> sektionComboBox;

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
        try {
            funktionComboBox.setItems(DatabaseManager.GetRoles());
            sektionComboBox.setItems(DatabaseManager.GetSektionen());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        InitTable();

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void InitTable() {
        InitCols();
    }

    private void InitCols() {
        name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        gender.setCellValueFactory(new PropertyValueFactory<>("Gender"));
        delete.setCellValueFactory(
                param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );

        EditableCols();
    }

    private void EditableCols() {
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(e -> e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue()));

        try {
            gender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
            gender.setCellFactory(ComboBoxTableCell.forTableColumn(DatabaseManager.GetGender()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
                        sektionComboBox.getSelectionModel().getSelectedItem(),
                        funktionComboBox.getSelectionModel().getSelectedItem()
                )));
    }

    private void UpdateData() throws SQLException {
        for (CandidatesDataModel candidatesDataModel : tableData.getItems()) {
            if (DatabaseManager.doesCandidateAlreadyExist(candidatesDataModel)) {
                continue;
            }

            DatabaseManager.insertNewCandidate(candidatesDataModel);
        }

        DatabaseManager.insertCandidateForRoleForSektion(
                tableData.getItems(),
                funktionComboBox.getSelectionModel().getSelectedItem(),
                sektionComboBox.getSelectionModel().getSelectedItem());
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
                sektionComboBox.getSelectionModel().getSelectedItem(),
                funktionComboBox.getSelectionModel().getSelectedItem());
        UpdateData();
        DatabaseManager.dumpUnusedCandidates();
    }

    @FXML
    private void onPrint(){
        // TODO: PRINT ELECTION
    }
}
