/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database;

import database.voting.calculators.meek.MeekCandidateDataModel;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class CandidatesDataModel {
    private SimpleStringProperty Name;
    private SimpleStringProperty Gender;
    protected CandidatesDataModel candidatesDataModel;

    public CandidatesDataModel(String name, String gender) {
        Name = new SimpleStringProperty(name);
        Gender = new SimpleStringProperty(gender);
        candidatesDataModel = this;
    }

    public CandidatesDataModel(CandidatesDataModel base){
        this(base.getName(), base.getGender());
    }

    public MeekCandidateDataModel convertToMeekCandidate(){
        return new MeekCandidateDataModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        CandidatesDataModel that = (CandidatesDataModel) o;
        return getName().equals(that.getName()) &&
                getGender().equals(that.getGender());
    }

    public String getName() {
        return Name.get();
    }

    public void setName(String name) {
        this.Name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return Name;
    }

    public String getGender() {
        return Gender.get();
    }

    public void setGender(String name) {
        this.Gender.set(name);
    }

    public SimpleStringProperty genderProperty() {
        return Gender;
    }

    public CandidatesDataModel getMe(){
        return this;
    }
}
