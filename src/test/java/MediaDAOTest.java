import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MediaDAOTest {

    @Test
    void testFindAllQueryDuration() throws DAOException {
        // Measure the time taken by the findAll method
        long startTime = System.currentTimeMillis();

        List<Object> medias = PersistanceFactory.createDAO(PersistanceType.DATABASE).createMovieDAO().findAll().stream()
                .map(movie -> (Media) movie).collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println(duration);

        // Assert that the query duration is less than 5 seconds
        assertTrue(duration < 5000, "The query took longer than 5 seconds");
    }

    @Test
    void testSaveRoom() throws DAOException {
        Room room = RoomFactory.createRoomInstance()
                .code(IntStream.range(0, 6)
                        .mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(new SecureRandom().nextInt(36)))
                        .map(Object::toString)
                        .collect(Collectors.joining()))
                .name("test")
                .creationDate(LocalDate.now())
                .mediaType(MediaType.MOVIE)
                .decade(1900)
                .allowedGenres(null)
                .allowedProviders(null)
                .allowedProductionCompanies(null)
                .roomMembers(Set.of(new RoomMember(new User("christian", "cecili", Gender.MALE, "chri0407@gmail.com", "psw30000."))))
                .build();

        long startTime = System.currentTimeMillis();
        PersistanceFactory.createDAO(PersistanceType.DATABASE).createRoomDAO().save(room);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println(duration);
        assertTrue(duration < 5000, "The query took longer than 5 seconds");
    }

}
