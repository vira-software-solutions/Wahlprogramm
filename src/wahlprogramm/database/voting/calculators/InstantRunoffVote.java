package database.voting.calculators;

import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.Map;

public class InstantRunoffVote implements PreferentialVoting {

    @Override
    public ArrayList<ResultsDataModel> calculate(int ballotCount, Map.Entry<String, ArrayList<RankingEntry>> ballotDatabase) {
        return null;
    }

    @Override
    public ArrayList<ResultsDataModel> calculateResults(String role, ArrayList<RankingEntry> rankingEntries) {
        return null;
    }
}
