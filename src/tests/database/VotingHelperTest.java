package database;

import database.voting.VotingHelper;
import helper.Helper;
import main.PropsManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tabs.election.rankingWindow.RankingEntry;
import tabs.electionPreparation.CandidatesDataModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VotingHelperTest {

    private static VotingHelper VHelper;

    @BeforeEach
    void setUp() throws IOException {
        PropsManager.init();
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
        assertTrue(VHelper.getBallotCountProperty().getValue() > 1);
        VHelper.reset();
        assertEquals(1, (int) VHelper.getBallotCountProperty().getValue());
    }

    @Test
    void addResults() {
        assertEquals(1, (int) VHelper.getBallotCountProperty().getValue());
        final ArrayList<RankingEntry> testData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testData.add(new RankingEntry().initData(new CandidatesDataModel(Helper.randStringGen(12), "Weiblich"), 0));
        }
        VHelper.addResults("Vorsitz", testData);
        assertTrue(VHelper.getBallotCountProperty().getValue() > 1);
    }
}