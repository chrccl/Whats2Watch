package dao_tests;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaDAOTest {

    @Test
    void testFindAllQueryDuration() throws DAOException {
        // Measure the time taken by the findAll method
        long startTime = System.currentTimeMillis();

        PersistanceFactory.createDAO(PersistanceType.FILESYSTEM).createMovieDAO()
                .findAll().stream()
                .map(movie -> (Media) movie);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert that the query duration is less than 5 seconds
        assertTrue(duration < 5000, "The query took longer than 5 seconds");
    }

}
