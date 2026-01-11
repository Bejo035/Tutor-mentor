package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.request.UpdateUserRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.repository.UserRepository;
import lombok.NonNull;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> implements UserDetailsService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private final ResourceService resourceService;

    public UserService(UserRepository repository, ResourceService resourceService) {
        super(repository);
        this.resourceService = resourceService;
    }

    public UserDb save(UserDb userDb) {
        return repository.save(userDb);
    }

    public UserDb addUser(UserRequest request) {
        return repository.save(new UserDb(request));
    }

    public UserDb updateUser(String id, UserRequest request) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        BeanUtils.copyProperties(request, userDb);// TODO here additional checks is needed!!!
        return repository.save(userDb);
    }

    public UserDb updateUser(Principal userPrincipal, UpdateUserRequest request, MultipartFile profilePhoto, MultipartFile cv) throws ResourceNotFoundException, BadRequestException {
        UserDb userDb = loadUserByUsername(userPrincipal.getName());
        if (userDb == null) {
            throw new ResourceNotFoundException("Could not find user for '%s' username".formatted(userPrincipal.getName()));
        }
        if (request != null) {
            BeanUtils.copyProperties(request, userDb);// TODO here additional checks is needed!!!
        }
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            updateProfilePhoto(profilePhoto, userDb);
        }
        if (cv != null && !cv.isEmpty()) {
            updateCv(cv, userDb);
        }

        return repository.save(userDb);
    }

    public List<UserDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public UserDb loadUserByUsername(String username) {
        return repository.findByUsername(username);
    }

    public boolean confirmUser(String id) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        if (userDb.isConfirmed()) {
            return false;
        }
        userDb.setConfirmed(true);
        repository.save(userDb);
        return true;
    }


    public void updateCv(MultipartFile cv, UserDb userDb) {
        try {
            ObjectId fileId = resourceService.uploadFile(cv);
            if (userDb.getCvId() != null) {
                resourceService.deleteResourceById(userDb.getProfileImageId());
            }

            userDb.setCvId(fileId.toString());
        } catch (IOException e) {
            LOGGER.warn("Error while uploading cv to database.");
        }
    }

    public void updateProfilePhoto(@NonNull MultipartFile profilePhoto, UserDb userDb) throws BadRequestException {
        if (!profilePhoto.getContentType().startsWith("image/")) {
            throw new BadRequestException("Invalid image type");
        }

        try {
            ObjectId fileId = resourceService.uploadFile(profilePhoto);
            if (userDb.getProfileImageId() != null) {
                resourceService.deleteResourceById(userDb.getProfileImageId());
            }

            userDb.setProfileImageId(fileId.toString());
        } catch (IOException e) {
            LOGGER.warn("Error while uploading image to database.");
        }
    }


    public List<UserResponse> getMentorsAndTutors() {
        Set<UserDb> result = new HashSet<>(repository.findAllByProgramRolesContains(UserProgramRole.MENTOR));
        result.addAll(repository.findAllByProgramRolesContains(UserProgramRole.TUTOR));

        return result.stream().map(UserDb::toUserResponse).toList();
    }
}
