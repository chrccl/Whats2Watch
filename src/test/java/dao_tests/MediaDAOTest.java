package dao_tests;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaDAOTest {

    @Test
    void testFindAllQueryDuration() throws DAOException {
        // Measure the time taken by the findAll method
        long startTime = System.currentTimeMillis();

        PersistenceFactory.createDAO(PersistenceType.FILESYSTEM).createMovieDAO()
                .findAll().stream()
                .map(movie -> (Media) movie);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert that the query duration is less than 3 seconds
        assertTrue(duration < 3000, "The query took longer than 3 seconds");
    }

}
