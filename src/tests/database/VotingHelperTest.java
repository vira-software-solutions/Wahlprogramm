package database;

import database.voting.VotingHelper;
import helper.Helper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tabs.election.rankingWindow.RankingEntry;
import tabs.electionPreparation.CandidatesDataModel;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VotingHelperTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        VotingHelper.reset();
    }

    @Test
    void reset() {
        final ArrayList<RankingEntry> testData = new ArrayList<>();
        testData.add(new RankingEntry());
        VotingHelper.addResults("Vorsitz", testData);
        assertTrue(VotingHelper.calculateResults().size() > 0);
        VotingHelper.reset();
        assertEquals(0, VotingHelper.calculateResults().size());
    }

    @Test
    void addResults() {
        assertEquals(0, VotingHelper.calculateResults().size());
        final ArrayList<RankingEntry> testData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testData.add(new RankingEntry().initData(new CandidatesDataModel(Helper.randStringGen(12), "Weiblich"), 0));
        }
        VotingHelper.addResults("Vorsitz", testData);
        assertTrue(VotingHelper.calculateResults().size() > 0);
    }

    @Test
    void addResult() {
        assertEquals(0, VotingHelper.calculateResults().size());
        VotingHelper.addResult("Vorsitz", new RankingEntry().initData(new CandidatesDataModel(Helper.randStringGen(12), "Weiblich"), 23));
        assertTrue(VotingHelper.calculateResults().size() > 0);
    }

    @Test
    void calculateResults() {
        // TODO engineer logic first.
    }
}