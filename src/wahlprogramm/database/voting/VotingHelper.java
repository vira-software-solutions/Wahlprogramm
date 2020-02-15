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

        for(BallotsOfOneRole ballotsOfOneRole : BALLOTS) {
            var votingSystemString = DatabaseManager
                    .getVotingOptionFromRole(ballotsOfOneRole.getRole()
                    );
            var votingSystem = PreferentialVotingFactory
                    .getPreferencialVoting(
                            votingSystemString
                    );

            int seats;
            switch (ballotsOfOneRole.getRole()) {
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

            if (votingSystem.equals(PreferentialVotingFactory.INSTANT_RUNOFF_VOTE)) {
                seats = 2;
            }

            var maleList = ballotsOfOneRole.getBallotsWithOnlyMales();
            var otherList = ballotsOfOneRole.getBallotsWithoutMales();

            if (!maleList.isEmpty()) {
                // Add every male winner:
                winningCandidates
                        .addAll(
                                votingSystem
                                        .calculateResults(
                                                new BallotsOfOneRole(
                                                        ballotsOfOneRole.getRole(),
                                                        maleList),
                                                otherList.isEmpty() ? seats : (int) Math.floor(((float) seats) / 2d))
                        );
            }

            if (!otherList.isEmpty()) {
                // Add every female and diverse winner:
                winningCandidates
                        .addAll(
                                votingSystem
                                        .calculateResults(
                                                new BallotsOfOneRole(
                                                        ballotsOfOneRole.getRole(),
                                                        otherList),
                                                maleList.isEmpty() ? seats : (int) Math.ceil(((float) seats) / 2d))
                        );
            }
        }

        return winningCandidates;
    }
}
