package database.voting.calculators;

import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.Map;

public interface PreferentialVoting {
    ArrayList<ResultsDataModel> calculate(int ballotCount, Map.Entry<String, ArrayList<RankingEntry>> ballotDatabase);
    ArrayList<ResultsDataModel> calculateResults(String role, ArrayList<RankingEntry> rankingEntries);
}
