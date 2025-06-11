package ge.batumi.tutormentor.manager;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.ProgramScheme;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.services.ProgramSchemeService;
import ge.batumi.tutormentor.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Management class for {@link ProgramScheme}.
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
     * @param programSchemeId {@link ProgramScheme} unique identifier to add user to.
     * @param userId          {@link UserDb} unique identifier to add to {@link ProgramScheme}.
     * @param userProgramRole {@link UserProgramRole} desired role to add user with.
     * @return Updated {@link UserProgramRole} object;
     * @throws ResourceNotFoundException If ether {@link UserDb} or {@link UserProgramRole} could not be found by specified ids.
     */
    public ProgramScheme addUserToProgramScheme(String programSchemeId, String userId, UserProgramRole userProgramRole) throws ResourceNotFoundException {
        Optional<ProgramScheme> programSchemeOptional = programSchemeService.findById(programSchemeId);
        Optional<UserDb> userDbOptional = userService.findById(userId);
        if (programSchemeOptional.isPresent() && userDbOptional.isPresent()) {
            Map<UserProgramRole, List<String>> programRoleToUserMap = programSchemeOptional.get().getUserProgramRoleToUserMap();
            programRoleToUserMap = addToUserProgramRoleToStringListMap(userId, userProgramRole, programRoleToUserMap);
            programSchemeOptional.get().setUserProgramRoleToUserMap(programRoleToUserMap);

            Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = userDbOptional.get().getProgramRoleToProgramSchemeMap();
            programRoleToProgramSchemeMap = addToUserProgramRoleToStringListMap(programSchemeId, userProgramRole, programRoleToProgramSchemeMap);
            userDbOptional.get().setProgramRoleToProgramSchemeMap(programRoleToProgramSchemeMap);

            userService.save(userDbOptional.get());
            ProgramScheme programScheme = programSchemeService.save(programSchemeOptional.get());
            LOGGER.info("Successfully added user (with id {}) to programScheme (with id {})", userId, programSchemeId);
            return programScheme;
        }
        String message = "Could not find user or programScheme with provided ids userId, programSchemeId: %s, %s".formatted(userId, programSchemeId);
        LOGGER.warn(message);
        throw new ResourceNotFoundException(message);
    }

    /**
     * Add provided String to provided {@link Map<UserProgramRole, List<String>>} object, if provided {@link Map<UserProgramRole, List<String>>} object is null create it and then add.
     *
     * @param toAdd                      String to add.
     * @param userProgramRole            Key to add String to.
     * @param programRoleToStringListMap {@link Map<UserProgramRole, List<String>>} object to add String to.
     * @return {@link Map<UserProgramRole, List<String>>} object with added string.
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

}
