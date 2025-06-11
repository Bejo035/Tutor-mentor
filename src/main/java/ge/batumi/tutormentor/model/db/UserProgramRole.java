package ge.batumi.tutormentor.model.db;

import lombok.Getter;

@Getter
public enum UserProgramRole {
    TUTOR("tutor"), MENTOR("mentor"), SEEKER("seeker");
    private final String name;

    UserProgramRole(String name) {
        this.name = name;
    }

}
