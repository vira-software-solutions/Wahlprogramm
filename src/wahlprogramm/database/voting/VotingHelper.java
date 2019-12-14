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
import java.util.*;

public class VotingHelper {
    public VotingHelper(){
        BallotCount = new SimpleIntegerProperty(1);
    }

    private final Set<BallotsOfOneRole> BALLOTS = new HashSet<>();
    private final SimpleIntegerProperty BallotCount;

    public SimpleIntegerProperty getBallotCountProperty(){return BallotCount;}

    public void reset() {
        BALLOTS.clear();
        BallotCount.setValue(1);
    }

    public void addResults(String role, ArrayList<RankingEntry> rankingEntries) {
        BallotCount.setValue(BallotCount.add(1).getValue());
        if (!BALLOTS.stream().anyMatch(b->b.getRole().equals(role))) {
            BALLOTS.add(new BallotsOfOneRole(role));
        }

        BALLOTS.stream().filter(b->b.getRole().equals(role)).findFirst().get().insertEntries(rankingEntries);
    }

    public ArrayList<ResultsDataModel> calculateResults() throws SQLException {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();

        for(BallotsOfOneRole ballotsOfOneRole : BALLOTS){
            winningCandidates.addAll(PreferentialVotingFactory
                    .getPreferencialVoting(
                            DatabaseManager
                                    .getVotingOptionFromRole(ballotsOfOneRole.getRole()
                                    )
                    ).calculateResults(ballotsOfOneRole));
        }

        return winningCandidates;
    }
}
