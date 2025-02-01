package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceFactory;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.DAODatabaseMedia;
import com.whats2watch.w2w.model.dao.entities.media.DAOFileSystemMedia;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SwipeController {

    private SwipeController() {
        throw new UnsupportedOperationException("SwipeController is a utility class and cannot be instantiated.");
    }

    public static List<Media> recommendMedias(Room room, RoomMember roomMember) throws EntityNotFoundException {
        try{
            DAO<Media, MediaId> mediaDAO = PersistenceFactory.createDAO(WhatsToWatch.getPersistenceType()).createMovieDAO();
            if(mediaDAO instanceof DAODatabaseMedia){
                return ((DAODatabaseMedia<? extends Media>)mediaDAO)
                        .findAllByOffset(computeOffset(roomMember))
                        .stream()
                        .filter(media -> avoidProposingSameMedia(roomMember, media))
                        .sorted((media1, media2) -> Double.compare(
                                calculateScore(roomMember, media2, room.getMediaType()), // Sort descending by score
                                calculateScore(roomMember, media1, room.getMediaType())))
                        .collect(Collectors.toList());
            }else {
                return ((DAOFileSystemMedia<? extends Media>)mediaDAO)
                        .findAll()
                        .stream()
                        .skip(computeOffset(roomMember))
                        .limit(20)
                        .filter(media -> avoidProposingSameMedia(roomMember, media))
                        .sorted((media1, media2) -> Double.compare(
                                calculateScore(roomMember, media2, room.getMediaType()), // Sort descending by score
                                calculateScore(roomMember, media1, room.getMediaType())))
                        .collect(Collectors.toList());
            }
        }catch (DAOException e){
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    private static int computeOffset(RoomMember roomMember) {
        SecureRandom random = new SecureRandom();
        int averageDecade = roomMember.getLikedMedia().stream()
                .map(media -> media.getMediaId().getYear()) // Get the year
                .mapToInt(Integer::intValue) // Convert to primitive int stream
                .average() // Calculate the average year
                .stream() // Convert OptionalDouble to a stream
                .mapToInt(avgYear -> (int) Math.round(avgYear / 10.0) * 10) // Round to the nearest decade
                .findFirst() // Get the first (and only) value from the stream
                .orElse(0); // Handle case where no years exist
        if(averageDecade != 0) {
            // Calculate the current decade
            int currentDecade = (LocalDate.now().getYear() / 10) * 10;
            // Calculate the exact number of 9-year intervals (as a double)
            double intervals = Math.abs(averageDecade - currentDecade) / 9.0;
            // Calculate the base number
            int baseNumber = (int) (intervals * 1000);
            // Generate a random number in the neighborhood (+-1000)
            return Math.abs(baseNumber + random.nextInt(2000) - 1000); //to avoid starvation of the algorithm
        }else{
            return random.nextInt(4000);    // Random Offset Fallback
        }
    }

    private static boolean avoidProposingSameMedia(RoomMember roomMember, Media media) {
        return !roomMember.getLikedMedia().contains(media) &&
                !roomMember.getPassedMedia().contains(media);
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
