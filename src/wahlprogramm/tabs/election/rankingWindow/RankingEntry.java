/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election.rankingWindow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingEntry extends HBox implements Initializable {
    @FXML
    private Label name;
    @FXML
    private Label gender;
    @FXML
    private Label rank;

    private RankedCandidateDataModel DataModel;

    RankingEntry initData(RankedCandidateDataModel dataModel) {
        DataModel = dataModel;
        return this;
    }

    RankedCandidateDataModel getRAnkedCandidateDataModel() {
        return this.DataModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setText(DataModel.getName());
        gender.setText(DataModel.getGender());
        rank.setText("" + DataModel.Rank.getValue());
    }
}
