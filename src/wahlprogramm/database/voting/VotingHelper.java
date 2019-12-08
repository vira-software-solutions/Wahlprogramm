/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database.voting;


import database.DatabaseManager;
import database.voting.calculators.PreferentialVotingFactory;
import javafx.beans.property.SimpleIntegerProperty;
import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VotingHelper {
    public VotingHelper(){
        BallotCount = new SimpleIntegerProperty(1);
    }

    private final HashMap<String, ArrayList<RankingEntry>> RESULTS = new HashMap<>();
    private final SimpleIntegerProperty BallotCount;

    public SimpleIntegerProperty getBallotCountProperty(){return BallotCount;}

    public void reset() {
        RESULTS.clear();
        BallotCount.setValue(1);
    }

    public void addResults(String role, ArrayList<RankingEntry> rankingEntries) {
        BallotCount.setValue(BallotCount.add(1).getValue());
        for (RankingEntry rankingEntry : rankingEntries) {
            addResult(role, rankingEntry);
        }
    }

    private void addResult(String role, RankingEntry rankingEntry) {
        if (rankingEntry == null) {
            return;
        }

        createOrAddData(role, rankingEntry);
    }

    private void createOrAddData(String role, RankingEntry rankingEntry) {
        if (!RESULTS.containsKey(role)) {
            RESULTS.put(role, new ArrayList<>());
        }

        RESULTS.get(role).add(rankingEntry);
    }

    public ArrayList<ResultsDataModel> calculateResults() throws SQLException {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();

        for (Map.Entry<String, ArrayList<RankingEntry>> set : RESULTS.entrySet()) {
            winningCandidates.addAll(
                    PreferentialVotingFactory.getPreferencialVoting(
                            DatabaseManager.getVotingOptionFromRole(set.getKey())).calculate(BallotCount.getValue(), set));
        }

        return winningCandidates;
    }
}
