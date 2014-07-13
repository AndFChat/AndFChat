package com.andfchat.core.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RelationManager {

    private final HashMap<CharRelation, Set<String>> relations = new HashMap<CharRelation, Set<String>>();

    @Inject
    public RelationManager() {
        for (CharRelation relation : CharRelation.values()) {
            relations.put(relation, new HashSet<String>());
        }
    }

    public boolean isOnList(CharRelation relation, FCharacter character) {
        return relations.get(relation).contains(character.getName());
    }

    public void addOnList(CharRelation relation, FCharacter character) {
        relations.get(relation).add(character.getName());
        character.addRelation(relation);
    }

    public void addRelationsToCharacter(FCharacter character) {
        for (CharRelation relation : CharRelation.values()) {
            if (relations.get(relation).contains(character.getName())) {
                character.addRelation(relation);
            }
        }
    }

    public void removeFromList(CharRelation relation, FCharacter character) {
        relations.get(relation).remove(character.getName());
        character.removeRelation(relation);
    }

    public void addCharacterToList(CharRelation relation, Set<String> charList) {
        relations.get(relation).addAll(charList);
    }

    public void addCharacterToList(CharRelation relation, String character) {
        relations.get(relation).add(character);
    }

    public Set<String> getRelationList(CharRelation relation) {
        return Collections.unmodifiableSet(relations.get(relation));
    }
}
