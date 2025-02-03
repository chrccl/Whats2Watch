package room_controller_tests;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class RoomMatcherTest {

    private final Media mediaA = MediaFactory.createMovieInstance().mediaId(new MediaId("A", 2010)).popularity(5.2).build();
    private final Media mediaB = MediaFactory.createMovieInstance().mediaId(new MediaId("B", 2005)).popularity(3.8).build();
    private final Media mediaC = MediaFactory.createMovieInstance().mediaId(new MediaId("C", 2015)).popularity(8.5).build();

    @Test
    void getRoomMatchesEmptyRoomMembersCase() {
        Room room = RoomFactory.createRoomInstance().build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRoomMatchesSingleMemberCase() {
        RoomMember member = new RoomMember(new User("", "", null, "", ""));
        member.getLikedMedia().addAll(Set.of(mediaA, mediaB, mediaC));
        Room room = RoomFactory.createRoomInstance().roomMembers(Set.of(member)).build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertEquals(List.of(mediaC, mediaA, mediaB), result);
    }

    @Test
    void getRoomMatchesTwoMembersWithCommonMediaCase() {
        RoomMember m1 = new RoomMember(new User("", "", null, "u1", ""));
        m1.getLikedMedia().addAll(Set.of(mediaA, mediaB));
        RoomMember m2 = new RoomMember(new User("", "", null, "u2", ""));
        m2.getLikedMedia().addAll(Set.of(mediaC, mediaB));
        Room room = RoomFactory.createRoomInstance().roomMembers(Set.of(m1, m2)).build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertEquals(List.of(mediaB), result);
    }

    @Test
    void getRoomMatchesMembersWithNoCommonMediaCase() {
        RoomMember m1 = new RoomMember(new User("", "", null, "u1", ""));
        m1.getLikedMedia().add(mediaA);
        RoomMember m2 = new RoomMember(new User("", "", null, "u2", ""));
        m2.getLikedMedia().addAll(Set.of(mediaC, mediaB));
        Room room = RoomFactory.createRoomInstance().roomMembers(Set.of(m1, m2)).build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRoomMatches_OneMemberWithEmptyLikedMedia_ReturnsEmptyList() {
        RoomMember m1 = new RoomMember(new User("", "", null, "", ""));
        m1.getLikedMedia().addAll(Collections.emptySet());
        Room room = RoomFactory.createRoomInstance().roomMembers(Set.of(m1)).build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRoomMatches_MemberWithEmptyAndNonEmptyLikedMedia_ReturnsEmptyList() {
        RoomMember m1 = new RoomMember(new User("", "", null, "u1", ""));
        m1.getLikedMedia().addAll(Collections.emptySet());
        RoomMember m2 = new RoomMember(new User("", "", null, "u2", ""));
        m2.getLikedMedia().addAll(Set.of(mediaC, mediaB));
        Room room = RoomFactory.createRoomInstance().roomMembers(Set.of(m1, m2)).build();
        List<Media> result = RoomController.getRoomMatches(room);
        assertTrue(result.isEmpty());
    }

}