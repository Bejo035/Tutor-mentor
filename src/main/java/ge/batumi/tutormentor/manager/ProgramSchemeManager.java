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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ProgramSchemeManager {
    private static final Logger LOGGER = LogManager.getLogger(ProgramSchemeManager.class);
    private final ProgramSchemeService programSchemeService;
    private final UserService userService;

    public ProgramScheme addUserToProgramScheme(String programSchemeId, String userId, UserProgramRole userProgramRole) throws ResourceNotFoundException {
        Optional<ProgramScheme> programSchemeOptional = programSchemeService.findById(programSchemeId);
        Optional<UserDb> userDbOptional = userService.findById(userId);
        if (programSchemeOptional.isPresent() && userDbOptional.isPresent()) {
            Map<UserProgramRole, List<String>> programRoleToUserMap = programSchemeOptional.get().getUserProgramRoleToUserMap();
            List<String> newUserListToReplace = programRoleToUserMap.get(userProgramRole);
            if (newUserListToReplace == null) {
                newUserListToReplace = List.of(programSchemeId);
            }

            programRoleToUserMap.put(userProgramRole, newUserListToReplace);
            programSchemeOptional.get().setUserProgramRoleToUserMap(programRoleToUserMap);

            Map<UserProgramRole, List<String>> programRoleToProgramSchemeMap = userDbOptional.get().getProgramRoleToProgramSchemeMap();
            List<String> newProgramSchemeListToReplace = programRoleToProgramSchemeMap.get(userProgramRole);
            if (newProgramSchemeListToReplace == null) {
                newProgramSchemeListToReplace = List.of(programSchemeId);
            }

            programRoleToProgramSchemeMap.put(userProgramRole, newProgramSchemeListToReplace);

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

}
