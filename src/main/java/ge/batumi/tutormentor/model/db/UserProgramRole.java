package ge.batumi.tutormentor.model.db;

public enum UserProgramRole {
    TUTOR("tutor"), MENTOR("mentor"), SEEKER("seeker");
    private String name;
    UserProgramRole(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
