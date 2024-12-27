package com.whats2watch.w2w.model;

import java.util.Objects;

public class Character {

    private String characterName;

    private Actor actor;

    public Character(String characterName, Actor actor) {
        this.characterName = characterName;
        this.actor = actor;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Character)) return false;

        Character character = (Character) o;
        return Objects.equals(getCharacterName(), character.getCharacterName()) &&
                Objects.equals(getActor(), character.getActor());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCharacterName());
        result = 31 * result + Objects.hashCode(getActor());
        return result;
    }

    @Override
    public String toString() {
        return "Character{" +
                "actor=" + actor.getFullName() +
                ", characterName='" + characterName + '\'' +
                '}';
    }
}
