package main;

import database.CandidatesDataModel;
import database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tabs.election.SektionDataModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public final class CollectionOfCollections {
    private static ObservableList<SektionDataModel> SEKTION_DATA_MODELS;
    private static ObservableList<String> ROLES;
    private static ObservableList<String> GENDER;
    private static ObservableList<CandidatesDataModel> CANDIDATES_DATAMODEL;
    private static Random RAND;

    public static void init(long seed) throws SQLException {
        SEKTION_DATA_MODELS = DatabaseManager.getSektionen();
        ROLES = DatabaseManager.getRoles();
        GENDER = DatabaseManager.getGender();
        RAND = new Random(seed);

        CANDIDATES_DATAMODEL = FXCollections.observableArrayList(new HashSet<>());
        updateCandidates();
    }

    public static void updateCandidates() throws SQLException {
        CANDIDATES_DATAMODEL.clear();

        for (var sektion :
                SEKTION_DATA_MODELS) {
            for (var role :
                    ROLES) {
                CANDIDATES_DATAMODEL.addAll(Objects.requireNonNull(DatabaseManager.getCandidatesForRole(sektion.getNum(), role)));
            }
        }
    }

    public static void removeCandidates(int sektion, String role) throws SQLException {
        if (SEKTION_DATA_MODELS.stream().anyMatch(s -> s.getNum() == sektion &&
                ROLES.stream().anyMatch(r -> r.equals(role)))) {
            CANDIDATES_DATAMODEL.removeAll(DatabaseManager.getCandidatesForRole(sektion, role));
        }
    }

    public static ObservableList<SektionDataModel> getSektionDataModels() {
        return SEKTION_DATA_MODELS;
    }

    public static ObservableList<String> getRoles() {
        return ROLES;
    }

    public static ObservableList<String> getGender() {
        return GENDER;
    }

    public static ObservableList<CandidatesDataModel> getCandidatesDataModel() {
        return CANDIDATES_DATAMODEL;
    }

    public static Random getRandom(){
        return RAND;
    }

    public static ObservableList<CandidatesDataModel> getCandidatesDatamodel(int sektion, String role) throws SQLException {
        if (SEKTION_DATA_MODELS.stream().anyMatch(s -> s.getNum() == sektion &&
                ROLES.stream().anyMatch(r -> r.equals(role)))) {
            var candidates = DatabaseManager.getCandidatesForRole(sektion, role);

            assert candidates != null;
            var toRet = CANDIDATES_DATAMODEL
                    .stream()
                    .filter(c -> candidates
                            .stream()
                            .anyMatch(gc -> gc.equals(c))
                    )
                    .collect(Collectors.toCollection(HashSet::new)
                    );

            return FXCollections.observableArrayList(toRet);
        }

        return null;
    }
}
