/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election;

import database.DatabaseManager;
import database.voting.calculators.Ballot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.CollectionOfCollections;
import main.MainController;
import tabs.election.rankingWindow.RankingWindow;
import database.CandidatesDataModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Election extends VBox {
    @FXML
    private VBox electionFields;

    @FXML
    private ComboBox<SektionDataModel> sektionComboBox;

    @FXML
    private Button processButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label counter;

    private ObservableList<RankingWindow> RankingWindows;

    @FXML
    private void initialize() {
        sektionComboBox.setItems(CollectionOfCollections.getSektionDataModels());

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
        counter.textProperty()
                .bind(sektionComboBox
                        .getSelectionModel()
                        .getSelectedItem()
                        .getVotingHelper()
                        .getBallotCountProperty()
                        .divide(RankingWindows.size())
                        .add(1)
                        .asString());
    }

    private void resetRankingController() throws SQLException, IOException {
        electionFields.getChildren().clear();

        ArrayList<RankingWindow> toObserve = new ArrayList<>();

        for (String role : CollectionOfCollections.getRoles()) {
            HashSet<CandidatesDataModel> rankedCandidateDataModels = new HashSet<>(Objects
                    .requireNonNull(CollectionOfCollections
                            .getCandidatesDatamodel(sektionComboBox
                                            .getSelectionModel()
                                            .getSelectedItem()
                                            .getNum(),
                                    role)));

            if (rankedCandidateDataModels.isEmpty()) {
                continue;
            }

            RankingWindow controller = new RankingWindow();
            controller.initData(role, FXCollections.observableArrayList(rankedCandidateDataModels));
            toObserve.add(controller);
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/tabs/election/rankingWindow/rankingWindow.fxml"));
            loader.setController(controller);

            electionFields.getChildren().add(loader.load());
        }

        RankingWindows = FXCollections.observableArrayList(toObserve);
    }

    @FXML
    private void onEnterBallot() throws IOException, SQLException {
        for (String role : CollectionOfCollections.getRoles()) {
            RankingWindow current = RankingWindows.stream().filter(rankingWindow -> rankingWindow.root.getText().equals(role)).findFirst().orElse(null);
            if (current == null) {
                continue;
            }

            sektionComboBox.getSelectionModel().getSelectedItem().getVotingHelper().addResult(role, new Ballot(current.getRankedCandidates()));
        }
        resetRankingController();
        this.resetButton.setDisable(false);
    }

    @FXML
    private void onReset() {
        sektionComboBox.getItems().forEach(s -> s.getVotingHelper().reset());
        this.resetButton.setDisable(true);
    }
}
