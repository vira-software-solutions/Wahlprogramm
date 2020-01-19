/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election.rankingWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import main.MainController;
import database.CandidatesDataModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RankingWindow extends VBox implements Initializable {
    private ObservableList<RankingEntry> rankingEntries;

    @FXML
    public TitledPane root;

    @FXML
    public ListView<RankingEntry> candidateListView;

    private String Heading;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.setText(Heading);
        loadData();

        candidateListView.setItems(rankingEntries);
        candidateListView.setOnMouseDragReleased(mouseDragEvent -> {
            int indexOfDraggingNode = candidateListView.getItems().indexOf(mouseDragEvent.getGestureSource());
            rotateNodes(candidateListView, indexOfDraggingNode, candidateListView.getItems().size() - 1);
        });
    }

    private void rotateNodes(final ListView root, final int indexOfDraggingNode,
                             final int indexOfDropTarget) {
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
            final Node node = (Node)root.getItems().remove(indexOfDraggingNode);
            root.getItems().add(indexOfDropTarget, node);
        }
    }

    private void loadData() {
        for (RankingEntry rankedCandidateDataModel : rankingEntries) {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/tabs/election/rankingWindow/rankingEntry.fxml"));
            loader.setController(rankedCandidateDataModel);
            loader.setRoot(rankedCandidateDataModel);

            try {
                addWithDragging(candidateListView, loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UpdateRankings();
    }

    private void addWithDragging(final ListView<RankingEntry> root, final RankingEntry node) {
        node.setOnDragDetected(mouseEvent -> {
            node.startFullDrag();
        });

        node.setOnMouseDragEntered(mouseDragEvent -> node.setStyle("-fx-background-color: accent_color"));

        node.setOnMouseDragExited(mouseDragEvent -> node.setStyle(""));

        node.setOnMouseDragReleased(mouseDragEvent -> {
            node.setStyle("");
            int indexOfDraggingNode = root.getItems().indexOf(mouseDragEvent.getGestureSource());
            int indexOfDropTarget = root.getItems().indexOf(node);
            rotateNodes(root, indexOfDraggingNode, indexOfDropTarget);

            UpdateRankings();

            mouseDragEvent.consume();
        });

        root.getItems().add(node);
    }

    private void UpdateRankings(){
        for (Node rankingEntry : rankingEntries){
            if(rankingEntry instanceof RankingEntry){
                ((RankingEntry)rankingEntry).setRank(rankingEntries.indexOf(rankingEntry)+1);
            }
        }
    }

    public void initData(String heading, ObservableList<CandidatesDataModel> candidatesDataModels) {
        Heading = heading;

        rankingEntries = FXCollections.observableList(new ArrayList<>());

        for (CandidatesDataModel candidatesDataModel : candidatesDataModels) {
            rankingEntries.add(new RankingEntry().initData(candidatesDataModel, -1));
        }
    }

    public ArrayList<RankingEntry> getRankedCandidates() {
        return new ArrayList<>(rankingEntries);
    }
}
