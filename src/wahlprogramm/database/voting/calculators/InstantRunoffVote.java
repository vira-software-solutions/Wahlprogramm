package database.voting.calculators;

import database.CandidatesDataModel;
import database.voting.BallotsOfOneRole;
import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstantRunoffVote implements PreferentialVoting {

    @Override
    public ArrayList<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole, int seats) {

        // initializing ArrayList of those candidates which are elected
        ArrayList<ResultsDataModel> winners = new ArrayList<ResultsDataModel>();

        // initializing the vote counter with every possible candidate
        HashMap<CandidatesDataModel, Double> voteCounter = new HashMap<CandidatesDataModel, Double>();

        // add every candidate for this specific role to the voteCounter
        var candidates = ballotsOfOneRole.Ballots.get(0).getCandidates();
        candidates.forEach(m -> voteCounter.put(m.getMe(), 0d));

        for (int i = 0; i < ballotsOfOneRole.Ballots.size(); i++) {
            Ballot currentBallot = ballotsOfOneRole.Ballots.get(i);

            // adding values to the voteCount according to the placement in the ballot
            // first place = 1; second place = 0.5 ...
            for (int j = 0; j < currentBallot.BallotEntries.size(); j++) {
                RankingEntry nextRanking = currentBallot.BallotEntries.get(j);
                double newVoteCount = voteCounter.get(nextRanking.getDataModel()) + 1d / (j + 1d);
                voteCounter.put(nextRanking.getDataModel(), newVoteCount);
            }

        }

        // checks for the candidate with the highest vote count
        CandidatesDataModel potentialWinner = null;
        ArrayList<CandidatesDataModel> allPotentialWinners = new ArrayList<CandidatesDataModel>();
        for (Map.Entry<CandidatesDataModel, Double> entry : voteCounter.entrySet()) {

            if (potentialWinner == null || entry.getValue().compareTo(voteCounter.get(potentialWinner)) > 0) {
                potentialWinner = entry.getKey();
            }
        }

        // checks if someone else has the same amount of votes
        for (Map.Entry<CandidatesDataModel, Double> entry : voteCounter.entrySet()) {
            if (entry.getValue().compareTo(voteCounter.get(potentialWinner)) == 0) {

                allPotentialWinners.add(entry.getKey().getMe());
            }

        }
        Random random = new Random();
        CandidatesDataModel randomlySelectedWinner = allPotentialWinners.get(random.nextInt(allPotentialWinners.size()));

        winners.add(new ResultsDataModel(randomlySelectedWinner, voteCounter.get(randomlySelectedWinner.getMe()), ballotsOfOneRole.getRole()));

        return winners;

    }
}
