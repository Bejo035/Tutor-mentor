package ge.batumi.tutormentor.exceptions;

/**
 * Checked exception thrown when a business rule or precondition is not satisfied.
 */
public class ExpectationsNotMet extends Exception {
    public ExpectationsNotMet(String message) {
        super(message);
    }
}
