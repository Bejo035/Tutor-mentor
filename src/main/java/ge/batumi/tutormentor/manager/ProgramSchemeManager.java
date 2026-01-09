package ge.batumi.tutormentor.manager;

import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramParticipant;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.response.ProgramSchemeFullResponse;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.model.response.UserData;
import ge.batumi.tutormentor.model.response.UserFullResponse;
import ge.batumi.tutormentor.services.ProgramParticipantService;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static final Logger LOGGER = LogManager.getLogger(ProgramSchemeManager.class);
    private final ProgramSchemeService programSchemeService;
    private final UserService userService;
    private final ProgramParticipantService programParticipantService;

    /**
     * Add user to programScheme and also update user's associated programSchemes.
     *
     * @param programSchemeId {@link ProgramSchemeDb} unique identifier to add user to.
     * @param userId          {@link UserDb} unique identifier to add to {@link ProgramSchemeDb}.
     * @param userProgramRole {@link UserProgramRole} desired role to add user with.
     * @return Updated {@link UserProgramRole} object;
     * @throws ResourceNotFoundException If ether {@link UserDb} or {@link UserProgramRole} could not be found by specified ids.
     */
    public ProgramSchemeDb addUserToProgramScheme(String programSchemeId, String userId, UserProgramRole userProgramRole) throws ResourceNotFoundException, ExpectationsNotMet {
        ProgramSchemeDb programSchemeDb = programSchemeService.findById(programSchemeId);

        ProgramSchemeDb.RegistrationDates registrationDates = programSchemeDb.getRegistrationDates();

        if (registrationDates != null) {
            if (LocalDateTime.now().isBefore(registrationDates.start())) {
                throw new ExpectationsNotMet("Registration to this program not started yet.");
            }

            if (LocalDateTime.now().isAfter(registrationDates.end())) {
                throw new ExpectationsNotMet("Registration to this program has ended.");
            }
        } else {
            LOGGER.warn("Could not find registrationDates in programScheme with '{}' id", programSchemeDb.getId());
        }
        UserDb userDb = userService.findById(userId);
        if (!userDb.getProgramRoles().contains(userProgramRole)) {
            LOGGER.warn("User with id '%s' does not have permission to be added in program scheme as '%s'".formatted(userId, userProgramRole));
            return programSchemeDb;
        }

        programParticipantService.enroll(userId, programSchemeId, userProgramRole);

        LOGGER.info("Successfully added user (with id {}) to programScheme (with id {})", userId, programSchemeId);
        return programSchemeDb;
    }

    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param programSchemeId The ProgramScheme id to get users for.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<UserProgramRole, List<UserDb>> getFullUserDetails(String programSchemeId) {
        List<ProgramParticipant> usersOfProgram = programParticipantService.getUsersOfProgram(programSchemeId);
        Map<UserProgramRole, List<ProgramParticipant>> programParticipantGroupedMap = usersOfProgram.stream().collect(Collectors.groupingBy(ProgramParticipant::getRole));
        Map<UserProgramRole, List<UserDb>> result = new HashMap<>();

        programParticipantGroupedMap.forEach((userProgramRole, programParticipants) -> {
            List<String> userIds = programParticipants.stream().map(ProgramParticipant::getUserId).toList();
            List<UserDb> allById = userService.findAllById(userIds);
            result.put(userProgramRole, allById);
        });

        return result;
    }


    public List<ProgramSchemeResponse> getAllAsProgramSchemeResponse(List<ProgramSchemeDb> programSchemeDbList) {
        return programSchemeDbList.stream().map(programSchemeDb -> {
            UserDb creatorUserDb;
            try {
                creatorUserDb = userService.findById(programSchemeDb.getCreatorUserId());
            } catch (ResourceNotFoundException | IllegalArgumentException e) {
                creatorUserDb = null;
            }

            return getProgramSchemeResponse(programSchemeDb, creatorUserDb);
        }).toList();
    }

    public List<UserFullResponse> getAllAsUserFullResponse(List<UserDb> userDbList) {
        return userDbList.stream().map(this::getAsUserFullResponse).toList();
    }

    private static ProgramSchemeResponse getProgramSchemeResponse(ProgramSchemeDb programSchemeDb, UserDb creatorUserDb) {
        return ProgramSchemeResponse.builder()
                .id(programSchemeDb.getId())
                .maxSize(programSchemeDb.getMaxSize())
                .creatorUserData(
                        (creatorUserDb != null)
                                ? new UserData(creatorUserDb.getId(), creatorUserDb.getName(), creatorUserDb.getSurname(), creatorUserDb.getWorkingPlace(), creatorUserDb.getWorkingPosition())
                                : null)
                .registrationDates(programSchemeDb.getRegistrationDates())
                .description(programSchemeDb.getDescription())
                .title(programSchemeDb.getTitle())
                .build();
    }

    public ProgramSchemeFullResponse getAsProgramSchemeFullResponse(ProgramSchemeDb programSchemeDb) {
        UserDb creatorUserDb;
        try {
            creatorUserDb = userService.findById(programSchemeDb.getCreatorUserId());
        } catch (ResourceNotFoundException e) {
            creatorUserDb = null;
        }

        Map<UserProgramRole, List<UserDb>> fullUserDetailsRaw = getFullUserDetails(programSchemeDb.getId());
        Map<UserProgramRole, List<UserData>> userProgramRoleToUserMap = new HashMap<>();
        fullUserDetailsRaw.forEach((userProgramRole, userDbs) -> userProgramRoleToUserMap.put(userProgramRole, userDbs.stream().map(UserDb::toUserData).toList()));

        return getProgramSchemeFullResponse(programSchemeDb, creatorUserDb, userProgramRoleToUserMap);
    }

    private static ProgramSchemeFullResponse getProgramSchemeFullResponse(ProgramSchemeDb programSchemeDb, UserDb creatorUserDb, Map<UserProgramRole, List<UserData>> userProgramRoleToUserMap) {
        return ProgramSchemeFullResponse.builder()
                .id(programSchemeDb.getId())
                .creatorUserData(
                        (creatorUserDb != null)
                                ? new UserData(creatorUserDb.getId(), creatorUserDb.getName(), creatorUserDb.getSurname(), creatorUserDb.getWorkingPlace(), creatorUserDb.getWorkingPosition())
                                : null)
                .registrationDates(programSchemeDb.getRegistrationDates())
                .description(programSchemeDb.getDescription())
                .maxSize(programSchemeDb.getMaxSize())
                .title(programSchemeDb.getTitle())
                .userProgramRoleToUserMap(userProgramRoleToUserMap)
                .build();
    }

    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param userId The user id to retrieve program scheme details for.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<UserProgramRole, List<ProgramSchemeDb>> getFullProgramSchemeDetails(String userId) {
        List<ProgramParticipant> usersOfProgram = programParticipantService.getProgramsOfUser(userId);
        Map<UserProgramRole, List<ProgramParticipant>> programParticipantGroupedMap = usersOfProgram.stream().collect(Collectors.groupingBy(ProgramParticipant::getRole));
        Map<UserProgramRole, List<ProgramSchemeDb>> result = new HashMap<>();

        programParticipantGroupedMap.forEach((userProgramRole, programParticipants) -> {
            List<String> programIds = programParticipants.stream().map(ProgramParticipant::getProgramId).toList();
            List<ProgramSchemeDb> allById = programSchemeService.findAllById(programIds);
            result.put(userProgramRole, allById);
        });

        return result;
    }

    public UserFullResponse getAsUserFullResponse(UserDb userDb) {
        Map<UserProgramRole, List<ProgramSchemeDb>> fullUserDetailsRaw = getFullProgramSchemeDetails(userDb.getId());
        Map<UserProgramRole, List<ProgramSchemeResponse>> userProgramRoleToUserMap = new HashMap<>();
        fullUserDetailsRaw.forEach((userProgramRole, programSchemeDbList) -> userProgramRoleToUserMap.put(userProgramRole, getAllAsProgramSchemeResponse(programSchemeDbList)));

        return getAsUserFullResponse(userDb, userProgramRoleToUserMap);
    }

    private static UserFullResponse getAsUserFullResponse(UserDb userDb, Map<UserProgramRole, List<ProgramSchemeResponse>> userProgramRoleToUserMap) {
        return UserFullResponse.builder()
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
                .programRoleToProgramSchemeMap(userProgramRoleToUserMap)
                .email(userDb.getEmail())
                .workingPosition(userDb.getWorkingPosition())
                .experience(userDb.getExperience())
                .username(userDb.getUsername())
                .build();
    }
}
