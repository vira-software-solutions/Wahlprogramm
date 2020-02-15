package database.voting.calculators.meek;

import database.CandidatesDataModel;

public class MeekCandidateDataModel extends CandidatesDataModel{
    private double weight = 0d;

    public MeekCandidateDataModel(CandidatesDataModel candidate){
        super(candidate);
    }

    public CandidatesDataModel getCandidateDataModel(){
        return this.candidatesDataModel;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double keepValue) {
        this.weight = keepValue;
    }
}
