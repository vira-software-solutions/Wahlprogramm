package tabs.election;

import database.voting.VotingHelper;
import javafx.beans.property.SimpleIntegerProperty;

public class SektionDataModel {
    private SimpleIntegerProperty Num;
    private VotingHelper VHelper;

    public SektionDataModel(int num) {
        Num = new SimpleIntegerProperty(num);
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
}
