package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;

import java.util.List;
import java.util.stream.Collectors;

public class SwipeController {

    private SwipeController() {
        throw new UnsupportedOperationException("SwipeController is a utility class and cannot be instantiated.");
    }

    public static List<Media> recommendMedias(Room room, RoomMember roomMember) throws DAOException {
        List<Media> allMedia = PersistanceFactory.createDAO(PersistanceType.DEMO)
                .createMovieDAO()
                .findAll()
                .stream()
                .map(movie -> (Media) movie)
                .collect(Collectors.toList());
        return allMedia.stream()
                .filter(media -> !hasUserAlreadyInteractedWithMedia(roomMember, media))
                .sorted((media1, media2) -> Double.compare(
                        calculateScore(roomMember, media2, room.getMediaType()),    // Sort descending by score
                        calculateScore(roomMember, media1, room.getMediaType())
                ))
                .limit(10)
                .collect(Collectors.toList());
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

        long commonActors = likedMedia.getCharacters().stream()
                .map(Character::getActor)
                .filter(actor -> media.getCharacters().stream()
                        .map(Character::getActor)
                        .anyMatch(actor::equals))
                .count();
        score += (int) (2 * commonActors); // Matching actors: Multiply by the number of actors in common

        if (mediaType == MediaType.MOVIE) {
            Movie likedMovie = (Movie) likedMedia;
            Movie candidateMovie = (Movie) media;
            if (likedMovie.getDirector().equals(candidateMovie.getDirector()))
                score += 2; // Director match adds a flat score
        }

        if (Math.abs(likedMedia.getMediaId().getYear() - media.getMediaId().getYear()) <= 10)
            score += 1; // Flat score for year similarity

        long commonProductionCompanies = likedMedia.getProductionCompanies().stream()
                .filter(media.getProductionCompanies()::contains).count();
        score += (int) commonProductionCompanies; // Matching production companies: Multiply by the number of companies in common

        return score;
    }
}
