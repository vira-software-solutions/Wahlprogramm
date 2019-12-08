/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database.voting;


import main.PropsManager;
import tabs.election.rankingWindow.RankingEntry;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class VotingHelper {
    private static final HashMap<String, ArrayList<RankingEntry>> RESULTS = new HashMap<>();

    public static void reset() {
        RESULTS.clear();
    }

    public static void addResults(String role, ArrayList<RankingEntry> rankingEntries) {
        for (RankingEntry rankingEntry : rankingEntries) {
            addResult(role, rankingEntry);
        }
    }

    public static void addResult(String role, RankingEntry rankingEntry) {
        if (rankingEntry == null) {
            return;
        }

        createOrAddData(role, rankingEntry);
    }

    private static void createOrAddData(String role, RankingEntry rankingEntry) {
        if (!RESULTS.containsKey(role)) {
            RESULTS.put(role, new ArrayList<>());
            RESULTS.get(role).add(rankingEntry);
        } else {
            RankingEntry currentRankingEntry = RESULTS.get(role)
                    .stream()
                    .filter(candidate -> candidate.DataModel.getName().equals(rankingEntry.DataModel.getName()) &&
                            candidate.DataModel.getGender().equals(rankingEntry.DataModel.getGender()))
                    .findFirst()
                    .orElse(null);

            if (currentRankingEntry != null) {
                currentRankingEntry.setRank(rankingEntry.getRank() + currentRankingEntry.getRank());
            }
        }
    }

    public static ArrayList<ResultsDataModel> calculateResults() {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();
        for (Map.Entry<String, ArrayList<RankingEntry>> set : RESULTS.entrySet()) {
            winningCandidates.add(calculateResult(set));
        }

        return winningCandidates;
    }

    private static ResultsDataModel calculateResult(Map.Entry<String, ArrayList<RankingEntry>> set) {
        final String male = (String) PropsManager.Props.get("voting.male");
        return new ResultsDataModel(set.getKey(),
                set.getValue().get(0).DataModel.getName(),
                set.getValue().get(0).DataModel.getGender(),
                set.getValue().get(0).getRank());
    }
}
