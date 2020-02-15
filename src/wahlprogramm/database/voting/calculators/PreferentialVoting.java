package database.voting.calculators;

import database.voting.BallotsOfOneRole;
import tabs.results.ResultsDataModel;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;

public interface PreferentialVoting {
    HashSet<ResultsDataModel> calculateResults(BallotsOfOneRole ballotsOfOneRole, int seats, Random rand);
}
