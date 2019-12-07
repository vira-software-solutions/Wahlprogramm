package database;

import helper.Helper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tabs.election.rankingWindow.RankedCandidateDataModel;

import java.lang.reflect.Array;
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
        final ArrayList<RankedCandidateDataModel> testData = new ArrayList<>();
        testData.add(new RankedCandidateDataModel("name", "gender"));
        VotingHelper.addResults("Vorsitz", testData);
        assertTrue(VotingHelper.calculateResults().size()>0);
        VotingHelper.reset();
        assertEquals(0, VotingHelper.calculateResults().size());
    }

    @Test
    void addResults() {
        assertEquals(0, VotingHelper.calculateResults().size());
        final ArrayList<RankedCandidateDataModel> testData = new ArrayList<>();
        for (int i=0;i<10;i++) {
            testData.add(new RankedCandidateDataModel(Helper.randStringGen(12), "Weiblich"));
        }
        VotingHelper.addResults("Vorsitz", testData);
        assertTrue(VotingHelper.calculateResults().size()>0);
    }

    @Test
    void addResult() {
        assertEquals(0, VotingHelper.calculateResults().size());
        VotingHelper.addResult("Vorsitz", new RankedCandidateDataModel(Helper.randStringGen(12), "Weiblich"));
        assertTrue(VotingHelper.calculateResults().size()>0);
    }

    @Test
    void calculateResults() {
        // TODO engineer logic first.
    }
}