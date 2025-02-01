package com.whats2watch.w2w.model.dao.entities.room;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.genre.DAODatabaseGenre;
import com.whats2watch.w2w.model.dao.entities.media.DAODatabaseMedia;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAODatabaseMovie;
import com.whats2watch.w2w.model.dao.entities.media.tvseries.DAODatabaseTVSeries;
import com.whats2watch.w2w.model.dao.entities.production_companies.DAODatabaseProductionCompany;
import com.whats2watch.w2w.model.dao.entities.user.DAODatabaseUser;
import com.whats2watch.w2w.model.dao.entities.watch_providers.DAODatabaseWatchProvider;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseRoom implements DAO<Room, String> {

    private final Connection conn;

    private static final String MEDIA_TYPE = "media_type";

    public DAODatabaseRoom(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Room entity) throws DAOException {
        String query = "INSERT INTO rooms (code, name, creation_date, media_type, decade) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), creation_date = VALUES(creation_date), " +
                "media_type = VALUES(media_type), decade = VALUES(decade);";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getCode());
            ps.setString(2, entity.getName());
            ps.setDate(3, Date.valueOf(entity.getCreationDate()));
            ps.setString(4, entity.getMediaType().name());
            ps.setInt(5, entity.getDecade() != null ? entity.getDecade() : 0);
            ps.executeUpdate();

            saveAssociations(entity);
        } catch (SQLException e) {
            throw new DAOException("Error saving room", e);
        }
    }

    @Override
    public Room findById(String entityKey) throws DAOException {
        String query = "SELECT code, name, creation_date, media_type, decade FROM rooms WHERE code = ?";
        Room room = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entityKey);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String roomCode = rs.getString("code");
                MediaType mediaType = MediaType.valueOf(rs.getString(MEDIA_TYPE));
                Room.RoomBuilder roomBuilder = new Room.RoomBuilder()
                        .code(roomCode)
                        .name(rs.getString("name"))
                        .creationDate(rs.getDate("creation_date").toLocalDate())
                        .mediaType(mediaType)
                        .decade((Integer) rs.getObject("decade"))
                        .allowedGenres(new DAODatabaseGenre(conn).findByRoomCode(entityKey))
                        .allowedProviders(new DAODatabaseWatchProvider(conn).findByRoomCode(entityKey))
                        .allowedProductionCompanies(new DAODatabaseProductionCompany(conn).findByRoomCode(entityKey));
                if (mediaType.equals(MediaType.MOVIE))
                    roomBuilder.roomMembers(findRoomMembers(roomCode, Movie.class));
                else
                    roomBuilder.roomMembers(findRoomMembers(roomCode, TVSeries.class));
                room = roomBuilder.build();
            }
        }catch(SQLException e){
            throw new DAOException("Error finding room by its code", e);
        }
        return room;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String query = "DELETE FROM rooms WHERE code = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entityKey);
            ps.executeUpdate();
        }catch(SQLException e){
            throw new DAOException("Error deleting room by its code", e);
        }
    }

    @Override
    public Set<Room> findAll() throws DAOException {
        String query = "SELECT code, name, creation_date, media_type, decade FROM rooms;";
        Set<Room> rooms = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)){
            while(rs.next()){
                String entityKey = rs.getString("code");
                MediaType mediaType = MediaType.valueOf(rs.getString(MEDIA_TYPE));
                Room.RoomBuilder roomBuilder = new Room.RoomBuilder()
                        .code(entityKey)
                        .name(rs.getString("name"))
                        .creationDate(rs.getDate("creation_date").toLocalDate())
                        .mediaType(MediaType.valueOf(rs.getString(MEDIA_TYPE)))
                        .decade((Integer) rs.getObject("decade"))
                        .allowedGenres(new DAODatabaseGenre(conn).findByRoomCode(entityKey))
                        .allowedProviders(new DAODatabaseWatchProvider(conn).findByRoomCode(entityKey))
                        .allowedProductionCompanies(new DAODatabaseProductionCompany(conn).findByRoomCode(entityKey));
                if (mediaType.equals(MediaType.MOVIE))
                    roomBuilder.roomMembers(findRoomMembers(entityKey, Movie.class));
                else
                    roomBuilder.roomMembers(findRoomMembers(entityKey, TVSeries.class));
                rooms.add(roomBuilder.build());
            }
        }catch(SQLException e){
            throw new DAOException("Error deleting room by its code", e);
        }
        return rooms;
    }

    public void updateLikedMedia(String roomCode, User user, Set<Media> media) throws DAOException {
        if(media.stream().allMatch(element -> element instanceof Movie)){
            addMediaToMember(roomCode, user.getEmail(), media, "room_member_liked_movies");
        }else{
            addMediaToMember(roomCode, user.getEmail(), media, "room_member_liked_tvseries");
        }
    }

    public void updatePassedMedia(String roomCode, User user, Set<Media> media) throws DAOException {
        if(media.stream().allMatch(element -> element instanceof Movie)){
            addMediaToMember(roomCode, user.getEmail(), media, "room_member_passed_movies");
        }else{
            addMediaToMember(roomCode, user.getEmail(), media, "room_member_passed_tvseries");
        }
    }

    private void saveAllowedGenres(Room room) throws SQLException {
        String deleteQuery = "DELETE FROM room_genre WHERE room_code = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, room.getCode());
            deletePs.executeUpdate();
        }

        String insertQuery = "INSERT INTO room_genre (room_code, genre) VALUES (?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (Genre genre : room.getAllowedGenres()) {
                insertPs.setString(1, room.getCode());
                insertPs.setString(2, genre.name());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveRoomMembers(Room room) throws SQLException {
        String deleteQuery = "DELETE FROM room_member WHERE room_code = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, room.getCode());
            deletePs.executeUpdate();
        }

        String insertQuery = "INSERT INTO room_member (room_code, user_email) VALUES (?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (RoomMember member : room.getRoomMembers()) {
                insertPs.setString(1, room.getCode());
                insertPs.setString(2, member.getUser().getEmail());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveAllowedProviders(Room room) throws SQLException {
        String deleteQuery = "DELETE FROM room_watch_provider WHERE room_code = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, room.getCode());
            deletePs.executeUpdate();
        }

        String insertQuery = "INSERT INTO room_watch_provider (room_code, watch_provider_name) VALUES (?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (WatchProvider provider : room.getAllowedProviders()) {
                insertPs.setString(1, room.getCode());
                insertPs.setString(2, provider.getProviderName());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveAllowedProductionCompanies(Room room) throws SQLException {
        String deleteQuery = "DELETE FROM room_production_company WHERE room_code = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, room.getCode());
            deletePs.executeUpdate();
        }

        String insertQuery = "INSERT INTO room_production_company (room_code, production_company_name) VALUES (?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (ProductionCompany company : room.getAllowedProductionCompanies()) {
                insertPs.setString(1, room.getCode());
                insertPs.setString(2, company.getCompanyName());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void addMediaToMember(String roomCode, String userEmail, Set<Media> mediaSet, String tableName) throws DAOException {
        String query = String.format("INSERT IGNORE INTO %s (room_code, user_email, title, year) VALUES (?, ?, ?, ?)", tableName);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, roomCode);
            ps.setString(2, userEmail);
            for (Media media : mediaSet) {
                ps.setString(3, media.getMediaId().getTitle());
                ps.setInt(4, media.getMediaId().getYear());
                ps.addBatch();
            }
            ps.executeBatch();
        }catch(SQLException e){
            throw new DAOException("Error saving media to room member: " + e.getMessage());
        }
    }

    private void saveAssociations(Room entity) throws DAOException {
        try{
            saveRoomMembers(entity);    //this doesn't need to save the User entity because all the user has to be registered to enter a room
            if(entity.getAllowedGenres() != null) saveAllowedGenres(entity);
            if(entity.getAllowedProductionCompanies() != null) saveAllowedProductionCompanies(entity);
            if(entity.getAllowedProviders()!= null) saveAllowedProviders(entity);
        }catch(SQLException e){
            throw new DAOException("Error saving associations JT of room: " + e.getMessage());
        }
    }

    private <T extends Media> Set<RoomMember> findRoomMembers(String roomCode, Class<T> mediaType) throws DAOException {
        String memberQuery = "SELECT user_email FROM room_member WHERE room_code = ?";
        Set<RoomMember> members = new HashSet<>();
        DAODatabaseMedia<? extends Media> mediaDAO = mediaType.equals(Movie.class) ?
                new DAODatabaseMovie(conn) :
                new DAODatabaseTVSeries(conn);

        try (PreparedStatement memberPs = conn.prepareStatement(memberQuery)) {
            memberPs.setString(1, roomCode);
            ResultSet memberRs = memberPs.executeQuery();

            while (memberRs.next()) {
                String userEmail = memberRs.getString("user_email");
                User user = new DAODatabaseUser(conn).findById(userEmail);
                RoomMember roomMember = new RoomMember(user);

                fetchLikedMedia(roomCode, user.getEmail(), roomMember, mediaDAO);
                fetchPassedMedia(roomCode, user.getEmail(), roomMember, mediaDAO);

                members.add(roomMember);
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching room members", e);
        }
        return members;
    }

    private void fetchLikedMedia(String roomCode, String userEmail, RoomMember roomMember, DAODatabaseMedia<? extends Media> mediaDAO) throws SQLException {
        String likedMediaQuery;
        if(mediaDAO.getClass().equals(DAODatabaseMovie.class)){
            likedMediaQuery = "SELECT title, year FROM room_member_liked_movies WHERE room_code = ? AND user_email = ?";
        }else{
            likedMediaQuery = "SELECT title, year FROM room_member_liked_tvseries WHERE room_code = ? AND user_email = ?";
        }

        try (PreparedStatement likedPs = conn.prepareStatement(likedMediaQuery)) {
            likedPs.setString(1, roomCode);
            likedPs.setString(2, userEmail);
            ResultSet likedRs = likedPs.executeQuery();

            while (likedRs.next()) {
                Media media = fetchMediaFromResultSet(likedRs, mediaDAO);
                roomMember.getLikedMedia().add(media);
            }
        }
    }

    private void fetchPassedMedia(String roomCode, String userEmail, RoomMember roomMember, DAODatabaseMedia<? extends Media> mediaDAO) throws SQLException {
        String passedMediaQuery;
        if(mediaDAO.getClass().equals(DAODatabaseMovie.class)){
            passedMediaQuery = "SELECT title, year FROM room_member_passed_movies WHERE room_code = ? AND user_email = ?";
        }else{
            passedMediaQuery = "SELECT title, year FROM room_member_passed_tvseries WHERE room_code = ? AND user_email = ?";
        }
        try (PreparedStatement passedPs = conn.prepareStatement(passedMediaQuery)) {
            passedPs.setString(1, roomCode);
            passedPs.setString(2, userEmail);
            ResultSet passedRs = passedPs.executeQuery();

            while (passedRs.next()) {
                Media media = fetchMediaFromResultSet(passedRs, mediaDAO);
                roomMember.getPassedMedia().add(media);
            }
        }
    }

    private Media fetchMediaFromResultSet(ResultSet rs, DAODatabaseMedia<? extends Media> mediaDAO) throws SQLException {
        String title = rs.getString("title");
        Integer year = rs.getInt("year");
        return mediaDAO.findById(new MediaId(title, year));
    }
}
