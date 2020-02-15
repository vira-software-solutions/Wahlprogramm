package main;

import database.DatabaseManager;
import javafx.collections.ObservableList;
import tabs.election.SektionDataModel;

import java.sql.SQLException;

public final class CollectionOfCollections {
    private static ObservableList<SektionDataModel> SEKTION_DATA_MODELS;
    private static ObservableList<String> ROLES;
    private static ObservableList<String> GENDER;

    public static void init() throws SQLException {
        SEKTION_DATA_MODELS = DatabaseManager.getSektionen();
        ROLES = DatabaseManager.getRoles();
        GENDER = DatabaseManager.getGender();
    }

    public static ObservableList<SektionDataModel>getSektionDataModels(){return SEKTION_DATA_MODELS;}
    public static ObservableList<String>getRoles(){return ROLES;}
    public static ObservableList<String>getGender(){return GENDER;}
}
