/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.election.rankingWindow;

import javafx.beans.property.SimpleIntegerProperty;
import tabs.electionPreparation.CandidatesDataModel;

public class RankedCandidateDataModel extends CandidatesDataModel {
    public SimpleIntegerProperty Rank;

    public RankedCandidateDataModel(String name, String gender) {
        super(name, gender);
        Rank = new SimpleIntegerProperty(0);
    }
}
