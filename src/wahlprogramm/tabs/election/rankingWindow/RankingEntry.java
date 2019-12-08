/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election.rankingWindow;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import tabs.electionPreparation.CandidatesDataModel;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingEntry extends HBox implements Initializable {
    @FXML
    private Label name;
    @FXML
    private Label gender;
    @FXML
    private Label rank;

    private SimpleIntegerProperty entryRank;

    public CandidatesDataModel DataModel;

    public RankingEntry initData(CandidatesDataModel dataModel, int rank) {
        entryRank = new SimpleIntegerProperty(rank);
        DataModel = dataModel;
        return this;
    }

    public int getRank(){return entryRank.getValue();}
    public void setRank(int rank){entryRank.setValue(rank);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setText(DataModel.getName());
        gender.setText(DataModel.getGender());
        rank.textProperty().bind(entryRank.asString());
    }
}
