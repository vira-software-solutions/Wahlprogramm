package database.voting.calculators;

import database.CandidatesDataModel;
import database.voting.BallotsOfOneRole;
import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;


import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class InstantRunoffVote implements PreferentialVoting {

    @Override
    public HashSet<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole, int seats, Random rand) {
        // initializing ArrayList of those candidates which are elected
        var winners = new HashSet<ResultsDataModel>();

        // initializing the vote counter with every possible candidate
        var voteCounter = new HashMap<CandidatesDataModel, Double>();

        // add every candidate for this specific role to the voteCounter
        var candidates = ballotsOfOneRole.Ballots.get(0).getCandidates();
        candidates.forEach(m -> voteCounter.put(m.getMe(), 0d));

        for (var ballot :
                ballotsOfOneRole.Ballots) {
            // adding values to the voteCount according to the placement in the ballot
            // first place = 1; second place = 0.5 ...
            for (int j = 0; j < ballot.BallotEntries.size(); j++) {
                RankingEntry nextRanking = ballot.BallotEntries.get(j);
                voteCounter.put(nextRanking.getDataModel(), voteCounter.get(nextRanking.getDataModel()) + 1d / (j + 1d));
            }
        }

        // while there has not been enough winners but still votes...
        while(winners.size()<seats&&!voteCounter.isEmpty()){
            // Calculate new max vote count
            var max = voteCounter.values().stream().max(Double::compareTo).get();
            // .. and get every new winner (containing said value) into a list of ResultsDataModels.
            var newWinner = voteCounter
                    .entrySet()
                    .stream()
                    .filter(p -> p.getValue()
                            .equals(max)
                    ).map(Map.Entry::getKey)
                    .collect(
                            Collectors.toCollection(HashSet::new)
                    );

            // while there would be too many winners (including the new ones)...
            var tempCopy = new ArrayList<>(newWinner);
            while(winners.size()+tempCopy.size()>seats){
                // randomly remove some of the new ones.
                tempCopy.remove(rand.nextInt(newWinner.size()));
            }

            // approve changes.
            newWinner = new HashSet<>(tempCopy);

            // add all the new winner to the winners list
            winners.addAll(
                    newWinner
                            .stream()
                            .map(nW->
                                    new ResultsDataModel(
                                            nW,
                                            (double)Math.round(voteCounter.get(nW) / ballotsOfOneRole.Ballots.size() * 10000000d) / 100000d,
                                            ballotsOfOneRole.getRole())
                            ).collect(
                                    Collectors.toCollection(HashSet::new)));

            // remove every new winner from the voting bank
            newWinner.forEach(nW->voteCounter.remove(nW.getMe()));
        }

        return winners;

    }
}
