/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.results;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import database.CandidatesDataModel;

public class ResultsDataModel extends CandidatesDataModel {
    private SimpleStringProperty Role;
    private SimpleDoubleProperty Percentage;

    public ResultsDataModel(String role, String name, String gender, double percentage) {
        super(name, gender);
        Role = new SimpleStringProperty(role);
        Percentage = new SimpleDoubleProperty(percentage);
    }

    public ResultsDataModel(String role, CandidatesDataModel candidatesDataModel, double percentage){
        this(role, candidatesDataModel.getName(), candidatesDataModel.getGender(), percentage);
    }

    public String getRole() {
        return Role.get();
    }

    public SimpleStringProperty roleProperty() {
        return Role;
    }

    public double getPercentage() {
        return Percentage.get();
    }

    public SimpleDoubleProperty percentageProperty() {
        return Percentage;
    }
}
