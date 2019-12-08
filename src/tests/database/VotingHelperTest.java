package database;

import database.voting.VotingHelper;
import helper.Helper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tabs.election.rankingWindow.RankingEntry;
import tabs.electionPreparation.CandidatesDataModel;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VotingHelperTest {

    private static VotingHelper VHelper;

    @BeforeEach
    void setUp() {
        VHelper = new VotingHelper();
    }

    @AfterEach
    void tearDown() {
        VHelper.reset();
    }

    @Test
    void reset() throws SQLException {
        final ArrayList<RankingEntry> testData = new ArrayList<>();
        testData.add(new RankingEntry());
        VHelper.addResults("Vorsitz", testData);
        assertTrue(VHelper.calculateResults().size() > 0);
        VHelper.reset();
        assertEquals(0, VHelper.calculateResults().size());
    }

    @Test
    void addResults() throws SQLException {
        assertEquals(0, VHelper.calculateResults().size());
        final ArrayList<RankingEntry> testData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testData.add(new RankingEntry().initData(new CandidatesDataModel(Helper.randStringGen(12), "Weiblich"), 0));
        }
        VHelper.addResults("Vorsitz", testData);
        assertTrue(VHelper.calculateResults().size() > 0);
    }

    @Test
    void addResult() throws SQLException {
        assertEquals(0, VHelper.calculateResults().size());
        VHelper.addResult("Vorsitz", new RankingEntry().initData(new CandidatesDataModel(Helper.randStringGen(12), "Weiblich"), 23));
        assertTrue(VHelper.calculateResults().size() > 0);
    }

    @Test
    void calculateResults() {
        // TODO engineer logic first.
    }
}