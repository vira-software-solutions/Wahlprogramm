package database.voting.calculators;

import database.CandidatesDataModel;
import database.voting.calculators.meek.MeekCandidateDataModel;
import tabs.election.rankingWindow.RankingEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Ballot {
    public final ArrayList<RankingEntry> BallotEntries;

    public Ballot(List<RankingEntry> ballotEntries) {
        BallotEntries = new ArrayList<>(ballotEntries);
    }

    public Ballot(){
        BallotEntries = new ArrayList<>();
    }


    public ArrayList<CandidatesDataModel> getCandidates() {
        return BallotEntries
                .stream()
                .map(b -> b.DataModel)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<MeekCandidateDataModel> getMeekCandidates(){
        return getCandidates()
                .stream()
                .map(CandidatesDataModel::convertToMeekCandidate)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
