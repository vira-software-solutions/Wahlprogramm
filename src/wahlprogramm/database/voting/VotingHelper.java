/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database.voting;


import database.DatabaseManager;
import database.voting.calculators.Ballot;
import database.voting.calculators.PreferentialVotingFactory;
import javafx.beans.property.SimpleIntegerProperty;
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

    public ArrayList<ResultsDataModel> calculateResults(int districtConferenceSeats, int districtParliamentSeats) throws SQLException {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();

        for(BallotsOfOneRole ballotsOfOneRole : BALLOTS){
            var votingSystem = PreferentialVotingFactory
                    .getPreferencialVoting(
                            DatabaseManager
                                    .getVotingOptionFromRole(ballotsOfOneRole.getRole()
                                    )
                    );

            int seats;
            switch (ballotsOfOneRole.getRole()){
                case "Bezirkskonferenz":
                    seats = districtConferenceSeats;
                    break;
                case "Bezirksrat":
                    seats = districtParliamentSeats;
                    break;
                default:
                    seats = 2;
                    break;
            }

            // Add every male winner:
            winningCandidates
                    .addAll(
                            votingSystem
                                    .calculateResults(
                                            new BallotsOfOneRole(
                                                    ballotsOfOneRole.getRole(),
                                                    ballotsOfOneRole.getBallotsWithOnlyMales()),
                                            (int)Math.floor(((float)seats)/2d))
                    );

            // Add every female and diverse winner:
            winningCandidates
                    .addAll(
                            votingSystem
                                    .calculateResults(
                                            new BallotsOfOneRole(
                                                    ballotsOfOneRole.getRole(),
                                                    ballotsOfOneRole.getBallotsWithoutMales()),
                                            (int)Math.ceil(((float)seats)/2d))
                    );
        }

        return winningCandidates;
    }
}
