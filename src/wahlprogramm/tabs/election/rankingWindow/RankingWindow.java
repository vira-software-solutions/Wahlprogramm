/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election.rankingWindow;

import com.sun.tools.javac.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RankingWindow extends VBox implements Initializable {
    private ObservableList<RankingEntry> rankingEntries;

    @FXML
    public Label headingText;

    @FXML
    private VBox candidateVBox;

    private String Haeding;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headingText.setText(Haeding);
        loadData();

        candidateVBox.setOnMouseDragReleased(mouseDragEvent -> {
            int indexOfDraggingNode = candidateVBox.getChildren().indexOf(mouseDragEvent.getGestureSource());
            rotateNodes(candidateVBox, indexOfDraggingNode, candidateVBox.getChildren().size() - 1);
        });
    }

    private void rotateNodes(final VBox root, final int indexOfDraggingNode,
                             final int indexOfDropTarget) {
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 0) {
            final Node node = root.getChildren().remove(indexOfDraggingNode);
            root.getChildren().add(indexOfDropTarget, node);
        }
    }

    private void loadData() {
        for (RankingEntry rankedCandidateDataModel : rankingEntries) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/tabs/election/rankingWindow/rankingEntry.fxml"));
            loader.setController(rankedCandidateDataModel);

            try {
                addWithDragging(candidateVBox, loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addWithDragging(final VBox root, final Node node) {
        node.setOnDragDetected(mouseEvent -> {
            node.startFullDrag();
            addPreview(root, node);
        });

        node.setOnMouseDragEntered(mouseDragEvent -> node.setStyle("-fx-background-color: accent_color"));

        node.setOnMouseDragExited(mouseDragEvent -> node.setStyle(""));

        node.setOnMouseDragReleased(mouseDragEvent -> {
            node.setStyle("");
            int indexOfDraggingNode = root.getChildren().indexOf(mouseDragEvent.getGestureSource());
            int indexOfDropTarget = root.getChildren().indexOf(node);
            rotateNodes(root, indexOfDraggingNode, indexOfDropTarget);

            removePreview(root);

            mouseDragEvent.consume();
        });

        root.getChildren().add(node);
    }

    private void addPreview(final VBox root, final Node node) {
        ImageView imageView = new ImageView(node.snapshot(null, null));
        imageView.setManaged(false);
        imageView.setMouseTransparent(true);
        root.getChildren().add(imageView);
        root.setUserData(imageView);
        root.setOnMouseDragged(event -> imageView.relocate(event.getX(), event.getY()));
        root.setOnMouseDragReleased(event -> removePreview(root));
    }

    private void removePreview(final VBox root) {
        root.setOnMouseDragged(null);
        root.getChildren().remove(root.getUserData());
        root.setUserData(null);
    }

    public void initData(String heading, ObservableList<RankedCandidateDataModel> rankedCandidates) {
        Haeding = heading;

        ArrayList<RankingEntry> toObserve = new ArrayList<>();
        for (RankedCandidateDataModel rankedCandidateDataModel : rankedCandidates) {
            toObserve.add(new RankingEntry().initData(rankedCandidateDataModel));
        }
        rankingEntries = FXCollections.observableList(toObserve);
    }

    public ArrayList<RankedCandidateDataModel> getRankedCandidates() {
        ArrayList<RankedCandidateDataModel> toRet = new ArrayList<>();
        for (RankingEntry rankingEntry : rankingEntries) {
            toRet.add(rankingEntry.getRAnkedCandidateDataModel());
        }

        return toRet;
    }
}
