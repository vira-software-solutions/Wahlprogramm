/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election;

import com.sun.tools.javac.Main;
import database.DatabaseManager;
import database.VotingHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import tabs.election.rankingWindow.RankedCandidateDataModel;
import tabs.election.rankingWindow.RankingWindow;
import tabs.electionPreparation.CandidatesDataModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Election extends VBox {
    @FXML
    private VBox electionFields;

    @FXML
    private ComboBox<Integer> sektionComboBox;

    @FXML
    private Button processButton;

    @FXML
    private Button resetButton;

    private ObservableList<String> Roles;
    private ObservableList<RankingWindow> RankingWindows;

    @FXML
    private void initialize(){
        try {
            sektionComboBox.setItems(DatabaseManager.GetSektionen());
            Roles = DatabaseManager.GetRoles();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.sektionComboBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            try {
                onSektionSelectionChange();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void onSektionSelectionChange() throws SQLException, IOException {
        if (sektionComboBox.getSelectionModel().getSelectedItem() != null) {
            processButton.setDisable(false);
        } else {
            processButton.setDisable(true);
        }

        resetRankingController();
    }

    private void resetRankingController() throws SQLException, IOException {
        electionFields.getChildren().clear();

        ArrayList<RankingWindow> toObserve = new ArrayList<>();

        for (String role : Roles) {
            ArrayList<RankedCandidateDataModel> rankedCandidateDataModels = new ArrayList<>();
            for (CandidatesDataModel candidatesDataModel : DatabaseManager.GetCandidatesForRole(sektionComboBox.getSelectionModel().getSelectedItem(), role)) {
                rankedCandidateDataModels.add(
                        new RankedCandidateDataModel(candidatesDataModel.getName(),
                                candidatesDataModel.getGender()));
            }

            if (rankedCandidateDataModels.isEmpty()) {
                continue;
            }

            RankingWindow controller = new RankingWindow();
            controller.initData(role, FXCollections.observableArrayList(rankedCandidateDataModels));
            toObserve.add(controller);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/tabs/election/rankingWindow/rankingWindow.fxml"));
            loader.setController(controller);

            electionFields.getChildren().add(loader.load());
        }

        RankingWindows = FXCollections.observableArrayList(toObserve);
    }

    @FXML
    private void onEnterBallot() throws IOException, SQLException {
        for (String role : Roles) {
            RankingWindow current = RankingWindows.stream().filter(rankingWindow -> rankingWindow.headingText.getText().equals(role)).findFirst().orElse(null);
            if (current == null) {
                continue;
            }

            VotingHelper.addResults(role, current.getRankedCandidates());
        }
        resetRankingController();

        this.resetButton.setDisable(false);
    }

    @FXML
    private void onReset() {
        VotingHelper.reset();
        this.resetButton.setDisable(true);
    }
}
