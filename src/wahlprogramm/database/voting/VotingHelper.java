/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database.voting;


import database.DatabaseManager;
import database.voting.calculators.Ballot;
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

    public void addResult(String role, Ballot ballot){
        BallotCount.setValue(BallotCount.add(1).getValue());
        if (BALLOTS.stream().noneMatch(b->b.getRole().equals(role))) {
            BALLOTS.add(new BallotsOfOneRole(role));
        }

        BALLOTS.stream().filter(b->b.getRole().equals(role)).findFirst().get().insertBallot(ballot);
    }

    public ArrayList<ResultsDataModel> calculateResults(int seats) throws SQLException {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();

        for(BallotsOfOneRole ballotsOfOneRole : BALLOTS){
            var votingSystem = PreferentialVotingFactory
                    .getPreferencialVoting(
                            DatabaseManager
                                    .getVotingOptionFromRole(ballotsOfOneRole.getRole()
                                    )
                    );

            // Add every male winner:
            winningCandidates
                    .addAll(
                            votingSystem
                                    .calculateResults(
                                            new BallotsOfOneRole(
                                                    ballotsOfOneRole.getRole(),
                                                    ballotsOfOneRole.getGenderSpecificBallots("Männlich")),
                                            (int)Math.floor(seats/2d))
                    );

            // Add every female and diverse winner:
            var femaleAndDiverseBallots = ballotsOfOneRole.getGenderSpecificBallots("Weiblich");
            femaleAndDiverseBallots.addAll(ballotsOfOneRole.getGenderSpecificBallots("Sonstige"));
            winningCandidates
                    .addAll(
                            votingSystem
                                    .calculateResults(
                                            new BallotsOfOneRole(
                                                    ballotsOfOneRole.getRole(),
                                                    femaleAndDiverseBallots),
                                            (int)Math.ceil(seats/2d))
                    );
        }

        return winningCandidates;
    }
}
