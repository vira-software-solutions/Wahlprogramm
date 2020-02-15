package database.voting.calculators.meek;

import database.voting.calculators.Ballot;

import java.util.ArrayList;

public class MeekBallot{
    public final ArrayList<MeekCandidateDataModel> MeekCandidates= new ArrayList<>();;

    public MeekBallot(Ballot ballot){
        if(ballot==null){
            return;
        }

        for (int i=0;i<ballot.BallotEntries.size();i++){
            MeekCandidates.add(i, new MeekCandidateDataModel(ballot.BallotEntries.get(i).DataModel));
        }
    }
}
