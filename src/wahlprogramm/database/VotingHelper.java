/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database;


import main.PropsManager;
import tabs.election.rankingWindow.RankedCandidateDataModel;
import tabs.results.ResultsDataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class VotingHelper {
    private static final HashMap<String, ArrayList<RankedCandidateDataModel>> RESULTS = new HashMap<>();

    public static void reset() {
        RESULTS.clear();
    }

    public static void addResults(String role, ArrayList<RankedCandidateDataModel> rankedCandidateDataModels) {
        for (RankedCandidateDataModel rankedCandidateDataModel : rankedCandidateDataModels) {
            addResult(role, rankedCandidateDataModel);
        }
    }

    public static void addResult(String role, RankedCandidateDataModel rankedCandidateDataModel) {
        if (rankedCandidateDataModel == null) {
            return;
        }

        createOrAddData(role, rankedCandidateDataModel);
    }

    private static void createOrAddData(String role, RankedCandidateDataModel rankedCandidateDataModel) {
        if (!RESULTS.containsKey(role)) {
            RESULTS.put(role, new ArrayList<>());
            RESULTS.get(role).add(rankedCandidateDataModel);
        } else {
            RankedCandidateDataModel currentCandidateDataModel = RESULTS.get(role)
                    .stream()
                    .filter(candidate -> candidate.getName().equals(rankedCandidateDataModel.getName()) &&
                            candidate.getGender().equals(rankedCandidateDataModel.getGender()))
                    .findFirst()
                    .orElse(null);

            if (currentCandidateDataModel != null) {
                currentCandidateDataModel.Rank.add(rankedCandidateDataModel.Rank);
            }
        }
    }

    public static ArrayList<ResultsDataModel> calculateResults() {
        ArrayList<ResultsDataModel> winningCandidates = new ArrayList<>();
        for (Map.Entry<String, ArrayList<RankedCandidateDataModel>> set : RESULTS.entrySet()) {
            winningCandidates.add(calculateResult(set));
        }

        return winningCandidates;
    }

    private static ResultsDataModel calculateResult(Map.Entry<String, ArrayList<RankedCandidateDataModel>> set) {
        final String male = (String) PropsManager.Props.get("voting.male");
        return new ResultsDataModel(set.getKey(),
                set.getValue().get(0).getName(),
                set.getValue().get(0).getGender(),
                100);
    }
}
