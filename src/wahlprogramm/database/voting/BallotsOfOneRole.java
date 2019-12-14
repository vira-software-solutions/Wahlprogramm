package database.voting;

import tabs.election.rankingWindow.RankingEntry;

import java.util.ArrayList;

public class BallotsOfOneRole {
    private final ArrayList<RankingEntry> BallotEntries;
    private final String Role;

    public BallotsOfOneRole(String role){
        BallotEntries = new ArrayList<>();
        Role = role;
    }

    public void insertEntry(RankingEntry rankingEntry){
        BallotEntries.add(rankingEntry);
    }

    public void insertEntries(ArrayList<RankingEntry> rankingEntries){
        BallotEntries.addAll(rankingEntries);
    }

    public ArrayList<RankingEntry> getBallotEntries() {
        return BallotEntries;
    }

    public String getRole() {
        return Role;
    }
}
