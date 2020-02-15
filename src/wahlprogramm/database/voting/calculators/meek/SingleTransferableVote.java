package database.voting.calculators.meek;

import database.CandidatesDataModel;
import database.voting.BallotsOfOneRole;
import database.voting.calculators.PreferentialVoting;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*
    This class is calculating the votes by using Meek's STV operations.
 */
public class SingleTransferableVote implements PreferentialVoting {

    @Override
    public ArrayList<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole, int seats) {
        final int VOTES = ballotsOfOneRole.Ballots.size();

        // Threshold = ( VOTES / SEATS + 1) + 1
        final double WINNING_THRESHOLD = (VOTES / (seats + 1d) + 1) - .1d;

        // Initializing (meek) ballots:
        var meekBallots = ballotsOfOneRole.Ballots.stream().map(MeekBallot::new).collect(Collectors.toList());
        meekBallots.forEach(m -> m.MeekCandidates.get(0).setWeight(1d));

        var meekCandidates = ballotsOfOneRole.Ballots.get(0).getMeekCandidates();

        // Initializing the voting-bank:
        var voteBank = new HashMap<CandidatesDataModel, Double>();
        meekCandidates.forEach(m -> voteBank.put(m.getCandidateDataModel(), 0d));

        var candidatesToBeRemovedFromBallots = new ArrayList<CandidatesDataModel>();
        var winner = new ArrayList<ResultsDataModel>();

        while (winner.size() < seats) {
            var excessVotes = voteBank.values().stream().mapToDouble(Double::doubleValue).sum();

            double worstValue = Double.MAX_VALUE, secondWorstValue = Double.MAX_VALUE;
            boolean done = false;
            for (var voteBankEntry :
                    voteBank.entrySet()) {
                // If there is a new worst value..
                if (voteBankEntry.getValue() < worstValue) {
                    // ... update them.
                    secondWorstValue = worstValue;
                    worstValue = voteBankEntry.getValue();
                }
            }

            // Check for winners:
            for (var mCandidate : meekCandidates){
                System.out.println(mCandidate.getWeight() + " vs " +WINNING_THRESHOLD);
                // check if this a new winner (which also has not been elected yet).
                if (voteBank.get(mCandidate.getCandidateDataModel()) >= WINNING_THRESHOLD &&
                        winner.stream().noneMatch(c -> c.getMe().equals(mCandidate.getCandidateDataModel()))) {
                    System.out.println("New winner; "+ mCandidate.getCandidateDataModel());
                    winner.add(new ResultsDataModel(
                            ballotsOfOneRole.getRole(),
                            mCandidate.getCandidateDataModel(),
                            mCandidate.getWeight()));

                    // If we have already achieved our seat size...
                    if(winner.size()>=seats){
                        // .. break.
                        done = true;
                        break;
                    }
                }
            }

            if(done){
                break;
            }

            // if the difference of the worst and second worst value is smaller than the excess votes...
            if ((secondWorstValue - worstValue) > excessVotes) {
                // ... calculate candidates which should be removed...
                final double finalWorstValue = worstValue;
                candidatesToBeRemovedFromBallots.addAll(
                        voteBank
                                .entrySet()
                                .stream()
                                .filter(e ->
                                        e.getValue().equals(finalWorstValue)
                                ).map(Map.Entry::getKey)
                                .collect(Collectors.toList()
                                )
                );

                // ... and remove them from every ballot.
                meekBallots
                        .forEach(mb ->
                                mb.MeekCandidates
                                        .removeAll(mb
                                                .MeekCandidates
                                                .stream()
                                                .filter(mc ->
                                                        candidatesToBeRemovedFromBallots
                                                                .contains(mc
                                                                        .getCandidateDataModel()
                                                                )
                                                ).collect(Collectors.toList())
                                        )
                        );
            }

            // For every ballot ...
            meekBallots.forEach(m -> {
                // ... for every meek candidate ...
                for (int i = 0; i < m.MeekCandidates.size(); i++) {
                    // ... check if their vote value is bigger or equal to the winning threshold..
                    if (voteBank.get(m.MeekCandidates.get(i).getCandidateDataModel()) >= WINNING_THRESHOLD) {
                        // ... if it is, then calculate the new weight by Threshold / Votes ...
                        final double newWeight = WINNING_THRESHOLD / voteBank.get(m.MeekCandidates.get(i));
                        // ... update it ...
                        m.MeekCandidates.get(i).setWeight(newWeight);
                        // ... and set the upcoming one (if available) to the inverse of said new weight.
                        if (i + 1 < m.MeekCandidates.size()) {
                            m.MeekCandidates.get(i + 1).setWeight(1 - newWeight);
                        }
                    }
                }
            });

            // Clear the vote bank.
            //voteBank.keySet().forEach(k -> voteBank.put(k, 0d));

            if(meekBallots.size()==0){
                // emergency break
                break;
            }

            System.out.println("breaking");
            break;
        }

        return winner;
    }
}