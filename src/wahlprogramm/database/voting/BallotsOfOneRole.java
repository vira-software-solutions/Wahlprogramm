package database.voting;

import database.voting.calculators.Ballot;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BallotsOfOneRole {
    public final ArrayList<Ballot> Ballots;
    private final String Role;

    public BallotsOfOneRole(String role) {
        this(role, new ArrayList<>());
    }

    public BallotsOfOneRole(String role, ArrayList<Ballot> ballots) {
        Ballots = ballots;
        Role = role;
    }

    public void insertBallot(Ballot ballot) {
        Ballots.add(ballot);
    }

    /*
        Returns ballots with only male entries.
     */
    public ArrayList<Ballot> getBallotsWithOnlyMales() {
        var genderedBallots = new ArrayList<Ballot>();

        Ballots.forEach(b -> {
            var filtered = b.BallotEntries
                    .stream()
                    .filter(bE ->
                            bE.DataModel
                                    .getGender()
                                    .equals("Männlich")
                    )
                    .collect(Collectors.toList());
            genderedBallots.add(new Ballot(filtered));
        });

        return genderedBallots;
    }

    /*
        Returns ballots without any male entries.
     */
    public ArrayList<Ballot> getBallotsWithoutMales() {
        var genderedBallots = new ArrayList<Ballot>();

        Ballots.forEach(b -> {
            var filtered = b.BallotEntries
                    .stream()
                    .filter(bE ->
                            !bE.DataModel
                                    .getGender()
                                    .equals("Männlich")
                    )
                    .collect(Collectors.toList());
            genderedBallots.add(new Ballot(filtered));
        });

        return genderedBallots;
    }

    public String getRole() {
        return Role;
    }
}
