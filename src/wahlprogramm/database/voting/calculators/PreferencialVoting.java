package database.voting.calculators;

import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.HashMap;

public interface PreferencialVoting {
    ArrayList<ResultsDataModel> calculate(HashMap<String, ArrayList<RankingEntry>> entrySet);
}
