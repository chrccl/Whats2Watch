package com.whats2watch.w2w.model;

public class RoomFactory {

    private RoomFactory() {
        throw new UnsupportedOperationException("RoomFactory is a utility class and cannot be instantiated.");
    }

    public static Room.RoomBuilder createRoomInstance() {
        return new Room.RoomBuilder();
    }

}
