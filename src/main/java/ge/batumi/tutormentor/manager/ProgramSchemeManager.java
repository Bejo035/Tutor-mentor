package ge.batumi.tutormentor.manager;

import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.*;
import ge.batumi.tutormentor.model.response.*;
import ge.batumi.tutormentor.services.CourseService;
import ge.batumi.tutormentor.services.ProgramParticipantService;
import ge.batumi.tutormentor.services.ProgramSchemeFileService;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Management class for {@link ProgramSchemeDb}.
 *
 * @author Luka Bezhanidze
 * @version 0.1
 * @since 0.1
 */
@RequiredArgsConstructor
@Component
public class ProgramSchemeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramSchemeManager.class);
    private final ProgramSchemeFileService programSchemeFileService;
    private final CourseService courseService;
    private final UserService userService;
    private final ProgramParticipantService programParticipantService;

    /**
     * Add user to course and also update user's associated courses.
     *
     * @param courseId        {@link Course} unique identifier to add user to.
     * @param userId          {@link UserDb} unique identifier to add to {@link Course}.
     * @param userProgramRole {@link UserProgramRole} desired role to add user with.
     * @return Updated {@link Course} object;
     * @throws ResourceNotFoundException If either {@link UserDb} or {@link Course} could not be found by specified ids.
     */
    public Course addUserToCourse(String courseId, String userId, UserProgramRole userProgramRole) throws ResourceNotFoundException, ExpectationsNotMet {
        Course course = courseService.findById(courseId);

        ProgramSchemeDb.RegistrationDates registrationDates = course.getRegistrationDates();

        if (registrationDates != null) {
            if (LocalDateTime.now().isBefore(registrationDates.start())) {
                throw new ExpectationsNotMet("Registration to this course has not started yet.");
            }

            if (LocalDateTime.now().isAfter(registrationDates.end())) {
                throw new ExpectationsNotMet("Registration to this course has ended.");
            }
        } else {
            LOGGER.warn("Could not find registrationDates in course with '{}' id", course.getId());
        }

        UserDb userDb = userService.findById(userId);
        if (!userDb.getProgramRoles().contains(userProgramRole)) {
            LOGGER.warn("User with id '%s' does not have permission to be added in course as '%s'".formatted(userId, userProgramRole));
            return course;
        }

        programParticipantService.enroll(userId, courseId, userProgramRole);

        LOGGER.info("Successfully added user (with id {}) to course (with id {})", userId, courseId);
        return course;
    }

    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a Course.
     *
     * @param courseId The Course id to get users for.
     * @return A map containing lists of users grouped by their role in the course.
     */
    public Map<UserProgramRole, List<UserDb>> getFullUserDetailsForCourse(String courseId) {
        List<CourseParticipant> usersOfCourse = programParticipantService.getUsersOfCourse(courseId);
        Map<UserProgramRole, List<CourseParticipant>> courseParticipantGroupedMap = usersOfCourse.stream().collect(Collectors.groupingBy(CourseParticipant::getRole));
        Map<UserProgramRole, List<UserDb>> result = new HashMap<>();

        courseParticipantGroupedMap.forEach((userProgramRole, courseParticipants) -> {
            List<String> userIds = courseParticipants.stream().map(CourseParticipant::getUserId).toList();
            List<UserDb> allById = userService.findAllById(userIds);
            result.put(userProgramRole, allById);
        });

        return result;
    }

    /**
     * Converts a list of {@link ProgramSchemeDb} entities to response DTOs, batch-fetching creator user data.
     */
    public List<ProgramSchemeResponse> getAllAsProgramSchemeResponse(List<ProgramSchemeDb> programSchemeDbList) {
        List<String> creatorIds = programSchemeDbList.stream()
                .map(ProgramSchemeDb::getCreatorUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<String, UserDb> creatorMap = userService.findAllById(creatorIds).stream()
                .collect(Collectors.toMap(UserDb::getId, Function.identity()));
        return programSchemeDbList.stream()
                .map(ps -> getProgramSchemeResponse(ps, creatorMap.get(ps.getCreatorUserId())))
                .toList();
    }

    /**
     * Converts a single {@link ProgramSchemeDb} entity to a response DTO, looking up the creator user.
     */
    public ProgramSchemeResponse getProgramSchemeResponse(ProgramSchemeDb programSchemeDb) {
        UserDb creatorUserDb;
        try {
            creatorUserDb = userService.findById(programSchemeDb.getCreatorUserId());
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            creatorUserDb = null;
        }
        return getProgramSchemeResponse(programSchemeDb, creatorUserDb);
    }

    /**
     * Converts a list of {@link UserDb} entities to {@link UserResponse} DTOs with course details.
     */
    public List<UserResponse> getAllAsUserResponse(List<UserDb> userDbList) {
        return userDbList.stream().map(this::getAsUserResponse).toList();
    }

    private ProgramSchemeResponse getProgramSchemeResponse(ProgramSchemeDb programSchemeDb, UserDb creatorUserDb) {
        ProgramSchemeResponse programSchemeResponse = ProgramSchemeResponse.builder()
                .id(programSchemeDb.getId())
                .creatorUserData(
                        (creatorUserDb != null)
                                ? new UserData(creatorUserDb.getId(), creatorUserDb.getName(), creatorUserDb.getSurname(), creatorUserDb.getWorkingPlace(), creatorUserDb.getWorkingPosition())
                                : null)
                .description(programSchemeDb.getDescription())
                .title(programSchemeDb.getTitle())
                .build();
        addAllProgramSchemeFilesToProgramSchemeResponse(programSchemeResponse);

        return programSchemeResponse;
    }

    /**
     * Populates the file-key-to-file-ID map on the given {@link ProgramSchemeResponse} from stored file records.
     */
    public void addAllProgramSchemeFilesToProgramSchemeResponse(ProgramSchemeResponse programSchemeResponse) {
        List<ProgramSchemeFileDb> programSchemeFileDbList = programSchemeFileService.findAllByProgramSchemeId(programSchemeResponse.getId());
        programSchemeFileDbList.forEach(programSchemeFileDb -> {
            if (programSchemeResponse.getKeyToFileIdsMap() == null) {
                programSchemeResponse.setKeyToFileIdsMap(new LinkedMultiValueMap<>());
            }

            programSchemeResponse.getKeyToFileIdsMap().add(programSchemeFileDb.getKey(), programSchemeFileDb.getFileId());
        });
    }


    /**
     * Converts a {@link ProgramSchemeDb} to a full response DTO including its associated courses.
     */
    public ProgramSchemeFullResponse getAsProgramSchemeFullResponse(ProgramSchemeDb programSchemeDb) {
        UserDb creatorUserDb;
        try {
            creatorUserDb = userService.findById(programSchemeDb.getCreatorUserId());
        } catch (ResourceNotFoundException e) {
            creatorUserDb = null;
        }

        List<Course> courses = courseService.getCoursesByProgramId(programSchemeDb.getId());
        List<CourseResponse> courseResponses = getAllAsCourseResponse(courses);

        return getProgramSchemeFullResponse(programSchemeDb, creatorUserDb, courseResponses);
    }

    private static ProgramSchemeFullResponse getProgramSchemeFullResponse(ProgramSchemeDb programSchemeDb, UserDb creatorUserDb, List<CourseResponse> courses) {
        return ProgramSchemeFullResponse.builder()
                .id(programSchemeDb.getId())
                .creatorUserData(
                        (creatorUserDb != null)
                                ? new UserData(creatorUserDb.getId(), creatorUserDb.getName(), creatorUserDb.getSurname(), creatorUserDb.getWorkingPlace(), creatorUserDb.getWorkingPosition())
                                : null)
                .description(programSchemeDb.getDescription())
                .title(programSchemeDb.getTitle())
                .courses(courses)
                .build();
    }

    /**
     * Retrieves full course details (Course objects) for a user.
     *
     * @param userId The user id to retrieve course details for.
     * @return A map containing lists of courses grouped by the user's role in the course.
     */
    public Map<UserProgramRole, List<Course>> getFullCourseDetails(String userId) {
        List<CourseParticipant> usersOfCourse = programParticipantService.getCoursesOfUser(userId);
        Map<UserProgramRole, List<CourseParticipant>> courseParticipantGroupedMap = usersOfCourse.stream().collect(Collectors.groupingBy(CourseParticipant::getRole));
        Map<UserProgramRole, List<Course>> result = new HashMap<>();

        courseParticipantGroupedMap.forEach((userProgramRole, courseParticipants) -> {
            List<String> courseIds = courseParticipants.stream().map(CourseParticipant::getCourseId).toList();
            List<Course> allById = courseService.findAllById(courseIds);
            result.put(userProgramRole, allById);
        });

        return result;
    }

    /**
     * Converts a {@link UserDb} entity to a {@link UserResponse} DTO with course participation details.
     */
    public UserResponse getAsUserResponse(UserDb userDb) {
        Map<UserProgramRole, List<Course>> fullCourseDetailsRaw = getFullCourseDetails(userDb.getId());
        Map<UserProgramRole, List<CourseResponse>> userProgramRoleToCourseMap = new HashMap<>();
        fullCourseDetailsRaw.forEach((userProgramRole, courseList) -> userProgramRoleToCourseMap.put(userProgramRole, getAllAsCourseResponse(courseList)));

        return getAsUserResponse(userDb, userProgramRoleToCourseMap);
    }

    /**
     * Converts a list of {@link Course} entities to response DTOs, batch-fetching creator user data.
     */
    public List<CourseResponse> getAllAsCourseResponse(List<Course> courseList) {
        List<String> creatorIds = courseList.stream()
                .map(Course::getCreatorUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<String, UserDb> creatorMap = userService.findAllById(creatorIds).stream()
                .collect(Collectors.toMap(UserDb::getId, Function.identity()));
        return courseList.stream()
                .map(course -> getCourseResponse(course, creatorMap.get(course.getCreatorUserId())))
                .toList();
    }

    private static CourseResponse getCourseResponse(Course course, UserDb creatorUserDb) {
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .maxSize(course.getMaxSize())
                .registrationDates(course.getRegistrationDates())
                .programId(course.getProgramId())
                .creatorUserData(
                        (creatorUserDb != null)
                                ? new UserData(creatorUserDb.getId(), creatorUserDb.getName(), creatorUserDb.getSurname(), creatorUserDb.getWorkingPlace(), creatorUserDb.getWorkingPosition())
                                : null)
                .build();
    }

    private UserResponse getAsUserResponse(UserDb userDb, Map<UserProgramRole, List<CourseResponse>> userProgramRoleToCourseMap) {
        UserResponse userResponse = UserResponse.builder()
                .id(userDb.getId())
                .userFeedback(userDb.getUserFeedback())
                .hobbies(userDb.getHobbies())
                .keywords(userDb.getKeywords())
                .confirmed(userDb.isConfirmed())
                .programRoles(userDb.getProgramRoles())
                .courseDescription(userDb.getCourseDescription())
                .name(userDb.getName())
                .motivation(userDb.getMotivation())
                .surname(userDb.getSurname())
                .strengths(userDb.getStrengths())
                .expectations(userDb.getExpectations())
                .workingPlace(userDb.getWorkingPlace())
                .year(userDb.getYear())
                .mentoringCourseName(userDb.getMentoringCourseName())
                .programRoleToCourseMap(userProgramRoleToCourseMap)
                .email(userDb.getEmail())
                .workingPosition(userDb.getWorkingPosition())
                .experience(userDb.getExperience())
                .username(userDb.getUsername())
                .rating(userDb.getRating())
                .build();
        userService.addAllUserFilesToUserResponse(userResponse);
        return userResponse;
    }
}
