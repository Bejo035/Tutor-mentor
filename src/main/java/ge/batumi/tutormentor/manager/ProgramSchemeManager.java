package ge.batumi.tutormentor.manager;

import ge.batumi.tutormentor.exceptions.ExpectationsNotMet;
import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramSchemeDb;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.response.ProgramSchemeFullResponse;
import ge.batumi.tutormentor.model.response.ProgramSchemeResponse;
import ge.batumi.tutormentor.model.response.UserData;
import ge.batumi.tutormentor.model.response.UserFullResponse;
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
        if (LocalDateTime.now().isBefore(registrationDates.start())) {
            throw new ExpectationsNotMet("Registration to this program not started yet.");
        }

        if (LocalDateTime.now().isAfter(registrationDates.end())) {
            throw new ExpectationsNotMet("Registration to this program has ended.");
        }

        UserDb userDb = userService.findById(userId);
        if (!userDb.getProgramRoles().contains(userProgramRole)) {
            LOGGER.warn("User with id '%s' does not have permission to be added in program scheme as '%s'".formatted(userId, userProgramRole));
            return programSchemeDb;
        }

        Map<UserProgramRole, List<String>> programRoleToUserMap = programSchemeDb.getUserProgramRoleToUserMap();
        programRoleToUserMap = addToUserProgramRoleToStringListMap(userId, userProgramRole, programRoleToUserMap);
        programSchemeDb.setUserProgramRoleToUserMap(programRoleToUserMap);

        Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = userDb.getProgramRoleToProgramSchemeMap();
        programRoleToProgramSchemeMap = addToUserProgramRoleToStringListMap(programSchemeId, userProgramRole, programRoleToProgramSchemeMap);
        userDb.setProgramRoleToProgramSchemeMap(programRoleToProgramSchemeMap);

        userService.save(userDb);
        ProgramSchemeDb updatedProgramSchemeDb = programSchemeService.save(programSchemeDb);
        LOGGER.info("Successfully added user (with id {}) to programScheme (with id {})", userId, programSchemeId);
        return updatedProgramSchemeDb;


    }

    /**
     * Add provided String to provided {@link Map} object, if provided {@link Map} object is null create it and then add.
     *
     * @param toAdd                      String to add.
     * @param userProgramRole            Key to add String to.
     * @param programRoleToStringListMap {@link Map} object to add String to.
     * @return {@link Map} object with added string.
     */
    private Map<UserProgramRole, List<String>> addToUserProgramRoleToStringListMap(String toAdd, UserProgramRole userProgramRole, Map<UserProgramRole, List<String>> programRoleToStringListMap) {
        if (programRoleToStringListMap == null) {
            programRoleToStringListMap = new HashMap<>();
        }
        List<String> newProgramSchemeListToReplace = programRoleToStringListMap.get(userProgramRole);
        if (newProgramSchemeListToReplace == null) {
            newProgramSchemeListToReplace = List.of(toAdd);
        } else {
            if (!newProgramSchemeListToReplace.contains(toAdd)) {
                newProgramSchemeListToReplace.add(toAdd);
            }
        }

        programRoleToStringListMap.put(userProgramRole, newProgramSchemeListToReplace);
        return programRoleToStringListMap;
    }


    /**
     * Retrieves full user details (UserDb objects) for mentors, tutors, and seekers of a ProgramScheme.
     *
     * @param programSchemeDb The ProgramScheme to retrieve user details for.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<UserProgramRole, List<UserDb>> getFullUserDetails(ProgramSchemeDb programSchemeDb) {
        Map<UserProgramRole, List<String>> userProgramRoleToUserMap = programSchemeDb.getUserProgramRoleToUserMap();
        Map<UserProgramRole, List<UserDb>> result = new HashMap<>();
        if (userProgramRoleToUserMap == null) {
            return result;
        }
        for (Map.Entry<UserProgramRole, List<String>> entry : userProgramRoleToUserMap.entrySet()) {
            result.put(entry.getKey(), userService.findAllById(entry.getValue()));
        }

        return result;
    }


    public List<ProgramSchemeResponse> getAllAsProgramSchemeResponse(List<ProgramSchemeDb> programSchemeDbList) {
        return programSchemeDbList.stream().map(programSchemeDb -> {
            UserDb creatorUserDb;
            try {
                creatorUserDb = userService.findById(programSchemeDb.getCreatorUserId());
            } catch (ResourceNotFoundException e) {
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

        Map<UserProgramRole, List<UserDb>> fullUserDetailsRaw = getFullUserDetails(programSchemeDb);
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
     * @param userDb The ProgramScheme to retrieve user details for.
     * @return A map containing lists of users grouped by their role in the scheme.
     */
    public Map<UserProgramRole, List<ProgramSchemeDb>> getFullProgramSchemeDetails(UserDb userDb) {
        Map<UserProgramRole, List<String>> userProgramRoleToProgramSchemeMap = userDb.getProgramRoleToProgramSchemeMap();
        Map<UserProgramRole, List<ProgramSchemeDb>> userProgramRoleListProgramSchemeMap = new HashMap<>();
        if (userProgramRoleToProgramSchemeMap == null) {
            return userProgramRoleListProgramSchemeMap;
        }
        for (Map.Entry<UserProgramRole, List<String>> entry : userProgramRoleToProgramSchemeMap.entrySet()) {
            userProgramRoleListProgramSchemeMap.put(entry.getKey(), programSchemeService.findAllById(entry.getValue()));
        }

        return userProgramRoleListProgramSchemeMap;
    }

    public UserFullResponse getAsUserFullResponse(UserDb userDb) {
        Map<UserProgramRole, List<ProgramSchemeDb>> fullUserDetailsRaw = getFullProgramSchemeDetails(userDb);
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
