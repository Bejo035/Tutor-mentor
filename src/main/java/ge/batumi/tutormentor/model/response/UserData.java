package ge.batumi.tutormentor.model.response;

/**
 * Lightweight user summary record embedded in other response DTOs (e.g. course creator).
 */
public record UserData(String id, String name, String surname, String workingPlace, String workingPosition) {
}
