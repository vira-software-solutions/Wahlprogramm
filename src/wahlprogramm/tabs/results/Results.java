/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.results;

import database.DatabaseManager;
import database.voting.VotingHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class Results extends VBox {

    @FXML
    private ComboBox<Integer> sektionComboBox;

    @FXML
    private Button exportButton;

    @FXML
    private TableView<ResultsDataModel> resultsTable;

    @FXML
    private TableColumn<ResultsDataModel, String> role;

    @FXML
    private TableColumn<ResultsDataModel, String> name;

    @FXML
    private TableColumn<ResultsDataModel, String> gender;

    @FXML
    private TableColumn<ResultsDataModel, Double> percentage;

    @FXML
    public void initialize() {
        try {
            this.sektionComboBox.setItems(DatabaseManager.GetSektionen());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.sektionComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((ov, t, t1) -> onSektionComboBoxChanged());

        InitCols();
    }

    private void InitCols() {
        this.name.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        this.role.setCellValueFactory(new PropertyValueFactory<>("Role"));
        this.percentage.setCellValueFactory(new PropertyValueFactory<>("percentage"));
    }

    private void onSektionComboBoxChanged() {
        if (sektionComboBox.getSelectionModel().getSelectedItem() != null) {
            exportButton.setDisable(false);
        } else {
            exportButton.setDisable(true);
        }

        resultsTable.setItems(FXCollections.observableArrayList(VotingHelper.calculateResults()));
    }

    @FXML
    private void onExport(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        final File toSave = fileChooser.showSaveDialog(((Node)event.getTarget()).getScene().getWindow());

        if (toSave != null) {
            try {
                final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toSave), StandardCharsets.UTF_8));
                writer.write(
                        "Ergebnisse fuer die Wahl von der Sektion " +
                                sektionComboBox.
                                        getSelectionModel().
                                        getSelectedIndex() +
                                ":" +
                                System.lineSeparator());

                writer.write("Funktion,Name,Geschlecht,Prozent" + System.lineSeparator());
                for (Object result : resultsTable.getItems().toArray()) {
                    ResultsDataModel resultsDataModel = (ResultsDataModel) result;
                    String text = resultsDataModel.getRole() + "," +
                            resultsDataModel.getName() + "," +
                            resultsDataModel.getGender() + "," +
                            resultsDataModel.getPercentage() + "\n";
                    writer.write(text);
                }

                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
