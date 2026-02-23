package ge.batumi.tutormentor.model.db;

import lombok.Getter;

/**
 * Roles a user can hold within a program: tutor, mentor, or seeker.
 */
@Getter
public enum UserProgramRole {
    TUTOR("tutor"), MENTOR("mentor"), SEEKER("seeker");
    private final String name;

    UserProgramRole(String name) {
        this.name = name;
    }

}
