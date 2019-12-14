package database.voting.calculators;

import database.voting.BallotsOfOneRole;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;

public interface PreferentialVoting {
    ArrayList<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole);
}
