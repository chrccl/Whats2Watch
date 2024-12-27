package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.DAODatabaseMedia;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SwipeController {

    private SwipeController() {
        throw new UnsupportedOperationException("SwipeController is a utility class and cannot be instantiated.");
    }

    public static List<Media> recommendMedias(Room room, RoomMember roomMember) throws DAOException {
        DAO<Media, MediaId> mediaDAO = PersistanceFactory.createDAO(PersistanceType.DATABASE).createMovieDAO();
        return ((DAODatabaseMedia<? extends Media>)mediaDAO)
                .findAllByOffset(computeOffset(roomMember))
                .stream()
                .filter(media -> !hasUserAlreadyInteractedWithMedia(roomMember, media))
                .sorted((media1, media2) -> Double.compare(
                        calculateScore(roomMember, media2, room.getMediaType()), // Sort descending by score
                        calculateScore(roomMember, media1, room.getMediaType())))
                .collect(Collectors.toList());
    }

    private static int computeOffset(RoomMember roomMember) {
        SecureRandom random = new SecureRandom();
        Integer mostCommonDecade = roomMember.getLikedMedia().stream()
                .map(media -> (media.getMediaId().getYear()/10)*10) // Calculate the decade
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // Count decades
                .entrySet().stream()    // Stream the entry set
                .max(Map.Entry.comparingByValue())  // Find the max by count
                .map(Map.Entry::getKey) // Get the decade
                .orElse(null);
        if(mostCommonDecade != null) {
            // Calculate the current decade
            int currentDecade = (LocalDate.now().getYear() / 10) * 10;
            // Calculate the exact number of 9-year intervals (as a double)
            double intervals = Math.abs(mostCommonDecade - currentDecade) / 9.0;
            // Calculate the base number
            int baseNumber = (int) (intervals * 1000);
            // Generate a random number in the neighborhood (+-500)
            return baseNumber + random.nextInt(1000) - 500; //to avoid starvation of the algorithm
        }else{
            return 1 + random.nextInt(4500);    // Random Offset Fallback
        }
    }

    private static boolean hasUserAlreadyInteractedWithMedia(RoomMember roomMember, Media media) {
        return roomMember.getLikedMedia().contains(media) ||
                roomMember.getPassedMedia().contains(media);
    }

    private static double calculateScore(RoomMember roomMember, Media media, MediaType mediaType) {
        int baseScore = roomMember.getLikedMedia().stream()
                .mapToInt(likedMedia -> calculateIndividualScore(likedMedia, media, mediaType))
                .sum();

        return baseScore * media.getPopularity() * media.getVoteAverage();
    }

    private static int calculateIndividualScore(Media likedMedia, Media media, MediaType mediaType) {
        int score = 0;
        long commonGenres = likedMedia.getGenres().stream()
                .filter(media.getGenres()::contains).count();
        score += (int) (3 * commonGenres); // Matching genres: Multiply by the number of genres in common

        score += likedMedia.getCharacters().stream()
                .map(Character::getActor)
                .filter(actor -> media.getCharacters().stream()
                        .map(Character::getActor)
                        .anyMatch(actor::equals))
                .collect(Collectors.teeing(
                        Collectors.counting(),
                        Collectors.averagingDouble(Actor::getPopularity),
                        (count, avgPopularity) -> count * avgPopularity * 2))
                .intValue();    // Matching actors: Multiply by the number of actors in common times their popularity

        if (mediaType == MediaType.MOVIE) {
            Movie likedMovie = (Movie) likedMedia;
            Movie candidateMovie = (Movie) media;
            if (likedMovie.getDirector().equals(candidateMovie.getDirector()))
                score += 2; // Director match adds a flat score
        }

        long commonProductionCompanies = likedMedia.getProductionCompanies().stream()
                .filter(media.getProductionCompanies()::contains).count();
        score += (int) commonProductionCompanies; // Matching production companies: Multiply by the number of companies in common

        return score;
    }
}
