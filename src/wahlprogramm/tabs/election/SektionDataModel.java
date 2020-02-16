package tabs.election;

import database.voting.VotingHelper;
import javafx.beans.property.SimpleIntegerProperty;

public class SektionDataModel {
    private SimpleIntegerProperty Num;
    private SimpleIntegerProperty BezR;
    private SimpleIntegerProperty BezK;

    private VotingHelper VHelper;

    public SektionDataModel(int num, int bezR, int bezK) {
        Num = new SimpleIntegerProperty(num);
        BezR = new SimpleIntegerProperty(bezR);
        BezK = new SimpleIntegerProperty(bezK);
        VHelper = new VotingHelper();
    }

    public int getNum() {
        return Num.get();
    }

    public SimpleIntegerProperty numProperty() {
        return Num;
    }

    public void setNum(int num){
        this.Num.setValue(num);
    }

    public VotingHelper getVotingHelper(){
        return this.VHelper;
    }

    @Override
    public String toString() {
        return ""+getNum();
    }

    public int getBezR() {
        return BezR.get();
    }

    public SimpleIntegerProperty bezRProperty() {
        return BezR;
    }

    public void setBezR(int bezR) {
        this.BezR.set(bezR);
    }

    public int getBezK() {
        return BezK.get();
    }

    public SimpleIntegerProperty bezKProperty() {
        return BezK;
    }

    public void setBezK(int bezK) {
        this.BezK.set(bezK);
    }
}
