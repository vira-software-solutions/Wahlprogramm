package database.voting.calculators.meek;

import database.voting.BallotsOfOneRole;
import database.voting.calculators.PreferentialVoting;
import database.voting.calculators.PreferentialVotingFactory;
import tabs.results.ResultsDataModel;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

/*
    This class is calculating the votes by using Meek's STV operations.
 */
public class SingleTransferableVote implements PreferentialVoting {

    @Override
    public HashSet<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole, int seats, Random rand){
        return Objects
                .requireNonNull(PreferentialVotingFactory
                .getPreferencialVoting(PreferentialVotingFactory.INSTANT_RUNOFF_VOTE))
                .calculateResults(
                        ballotsOfOneRole,
                        seats,
                        rand);
    }
}