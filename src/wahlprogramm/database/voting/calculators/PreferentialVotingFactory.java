package database.voting.calculators;

import database.voting.calculators.meek.SingleTransferableVote;

public final class PreferentialVotingFactory {
    public static final String INSTANT_RUNOFF_VOTE="INSTANT_RUNOFF_VOTE";
    public static final String SINGLE_TRANSFERABLE_VOTE="SINGLE_TRANSFERABLE_VOTE";

    public static final PreferentialVoting getPreferencialVoting(String selection){
        switch (selection){
            case INSTANT_RUNOFF_VOTE:
                return new InstantRunoffVote();
            case SINGLE_TRANSFERABLE_VOTE:
                return new SingleTransferableVote();
            default:
                return null;
        }
    }
}
