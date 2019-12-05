/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package tabs.electionPreparation;

import javafx.beans.property.SimpleStringProperty;

public class CandidatesDataModel {
    private SimpleStringProperty Name;
    private SimpleStringProperty Gender;

    public CandidatesDataModel(String name, String gender) {
        Name = new SimpleStringProperty(name);
        Gender = new SimpleStringProperty(gender);
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
}
