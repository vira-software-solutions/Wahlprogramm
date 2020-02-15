package database;

import database.voting.calculators.PreferentialVotingFactory;
import helper.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.CollectionOfCollections;
import main.PropsManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tabs.election.SektionDataModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() throws IOException, SQLException {
        PropsManager.init();

        final User user = new User(Helper.randStringGen(15), Helper.randStringGen(20));
        DatabaseManager.initializeDatabase(user);

        CollectionOfCollections.init(LocalDateTime.now().getNano());
    }

    @AfterEach
    void tearDown() {
        if(DatabaseManager.databaseExists()){
            new File(DatabaseManager.DATABASE_LOCATION).delete();
        }
    }

    @Test
    void databaseExists() throws IOException {
        if(DatabaseManager.databaseExists()){
            new File(DatabaseManager.DATABASE_LOCATION).delete();
        }
        assertFalse(DatabaseManager.databaseExists());
        new PrintWriter(DatabaseManager.DATABASE_LOCATION, StandardCharsets.UTF_8);
        assertTrue(DatabaseManager.databaseExists());
    }

    @Test
    void initializeDatabase() throws SQLException {
        if(DatabaseManager.databaseExists()){
            new File(DatabaseManager.DATABASE_LOCATION).delete();
        }

        final User user = new User(Helper.randStringGen(15), Helper.randStringGen(20));
        DatabaseManager.initializeDatabase(user);
        assertTrue(DatabaseManager.databaseExists());
        assertTrue(DatabaseManager.confirmUser(user));
    }

    @Test
    void insertUser() throws SQLException {
        final User admin2 = new User(Helper.randStringGen(15), Helper.randStringGen(20));

        DatabaseManager.insertUser(admin2);
        assertTrue(DatabaseManager.confirmUser(admin2));
    }

    @Test
    void insertCandidateForRoleForSektion() throws SQLException {
        final int sektion = 1;
        final String funktion = "Vorsitz";
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(12),"Sonstige");

        DatabaseManager.insertCandidateForRoleForSektion(
                FXCollections.observableArrayList(testCandidatesDataModel),
                funktion,
                sektion);

        var got = DatabaseManager.getCandidatesForRole(sektion, funktion).get(0);

        assertEquals(got.getGender(), testCandidatesDataModel.getGender());
        assertEquals(got.getName(), testCandidatesDataModel.getName());
    }

    @Test
    void insertNewCandidate() throws SQLException {
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(12),"Weiblich");
        assertFalse(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
        DatabaseManager.insertNewCandidate(testCandidatesDataModel);
        assertTrue(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
    }

    @Test
    void confirmUser() throws SQLException {
        final User testUser = new User(Helper.randStringGen(23), Helper.randStringGen(22));
        assertFalse(DatabaseManager.confirmUser(testUser));
        DatabaseManager.insertUser(testUser);
        assertTrue(DatabaseManager.confirmUser(testUser));
    }

    @Test
    void doesCandidateAlreadyExist() throws SQLException {
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(12),"Männlich");
        assertFalse(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
        DatabaseManager.insertNewCandidate(testCandidatesDataModel);
        assertTrue(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
    }

    @Test
    void getRoles() throws SQLException {
        final ObservableList<String> roles = CollectionOfCollections.getRoles();
        for (String s : Set.of(
                "Vorsitz",
                "Finanzreferat",
                "Schriftführung",
                "Katasterführung",
                "Frauenreferat",
                "Bildungsreferat",
                "Umweltreferat",
                "Jugendreferat",
                "Migrationsreferat",
                "Medienreferat",
                "Gewerkschaftsreferat",
                "Wirtschaftsreferat",
                "Bezirkskonferenz",
                "Bezirksrat")) {
            assertTrue(roles.contains(s));
        }
    }

    @Test
    void getSektionen(){
        final ObservableList<SektionDataModel> sektionen = CollectionOfCollections.getSektionDataModels();
        for(int i : Set.of(
                1,
                2,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                18,
                20,
                22,
                26,
                29,
                31,
                32,
                33,
                34)){
            assertTrue(sektionen.stream().anyMatch(s->s.getNum()==i));
        }
    }

    @Test
    void getCandidatesForRole() throws SQLException {
        final int sektion = 1;
        final String funktion = "Umweltreferat";
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(45),"Weiblich");

        assertEquals(DatabaseManager.getCandidatesForRole(sektion, funktion).size(), 0);

        DatabaseManager.insertCandidateForRoleForSektion(
                FXCollections.observableArrayList(testCandidatesDataModel),
                funktion,
                sektion);

        assertNotEquals(DatabaseManager.getCandidatesForRole(sektion, funktion).size(), 0);

    }

    @Test
    void getGender() {
        final String[] roles = CollectionOfCollections.getGender().toArray(String[]::new);
        assertArrayEquals(roles, new String[]{
                "Männlich",
                "Weiblich",
                "Sonstige"});
    }

    @Test
    void dumpCandidatesForRoleOfSektion() throws SQLException {
        final int sektion = 1;
        final String funktion = "Umweltreferat";
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(45),"Sonstige");

        DatabaseManager.insertCandidateForRoleForSektion(
                FXCollections.observableArrayList(testCandidatesDataModel),
                funktion,
                sektion);

        assertNotEquals(DatabaseManager.getCandidatesForRole(sektion, funktion).size(), 0);
        DatabaseManager.dumpCandidatesForRoleOfSektion(sektion, funktion);
        assertEquals(DatabaseManager.getCandidatesForRole(sektion, funktion).size(), 0);
    }

    @Test
    void dumpUnusedCandidates() throws SQLException {
        final CandidatesDataModel testCandidatesDataModel = new CandidatesDataModel(Helper.randStringGen(12),"Weiblich");
        DatabaseManager.insertNewCandidate(testCandidatesDataModel);
        assertTrue(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
        DatabaseManager.dumpUnusedCandidates();
        assertFalse(DatabaseManager.doesCandidateAlreadyExist(testCandidatesDataModel));
    }

    @Test
    void getBlacklistedGendersForRole() throws SQLException{
        assertEquals(DatabaseManager.getBlacklistedGendersForRole("Finanzreferat").size(), 0);
        assertEquals(DatabaseManager.getBlacklistedGendersForRole("Frauenreferat").get(0), "Männlich");
    }

    @Test
    void getVotingOptionFromRole() throws SQLException {
        assertEquals(DatabaseManager.getVotingOptionFromRole("Umweltreferat"), PreferentialVotingFactory.INSTANT_RUNOFF_VOTE);
        assertEquals(DatabaseManager.getVotingOptionFromRole("Bezirkskonferenz"), PreferentialVotingFactory.SINGLE_TRANSFERABLE_VOTE);
    }
}