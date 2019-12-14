package database.voting.calculators;

import database.voting.BallotsOfOneRole;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;

public class SingleTransferableVote implements PreferentialVoting {
    @Override
    public ArrayList<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole) {
        return null;
    }
}
