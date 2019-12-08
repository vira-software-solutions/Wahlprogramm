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
import main.CollectionOfCollections;
import tabs.election.SektionDataModel;

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
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
}
