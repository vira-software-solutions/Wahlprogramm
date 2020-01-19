package database.voting;

import database.voting.calculators.Ballot;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BallotsOfOneRole {
    public final ArrayList<Ballot> Ballots;
    private final String Role;

    public BallotsOfOneRole(String role){
        this(role, new ArrayList<>());
    }

    public BallotsOfOneRole(String role, ArrayList<Ballot> ballots){
        Ballots = ballots;
        Role = role;
    }

    public void insertBallot(Ballot ballot){
        Ballots.add(ballot);
    }

    public ArrayList<Ballot> getGenderSpecificBallots(String gender){
        var maleBallots = new ArrayList<Ballot>();
        for(var ballot:
                Ballots){
            maleBallots.add(
                    new Ballot(
                            ballot
                                    .BallotEntries
                                    .stream()
                                    .filter(bE->
                                            bE
                                                    .DataModel
                                                    .getGender()
                                                    .equals(gender)
                                    )
                                    .collect(Collectors.toList())
                    )
            );
        }

        return maleBallots;
    }

    public String getRole() {
        return Role;
    }
}
