package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserFileDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.request.UpdateUserRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> implements UserDetailsService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private final ResourceService resourceService;
    private final UserFileService userFileService;

    public UserService(UserRepository repository, ResourceService resourceService, UserFileService userFileService) {
        super(repository);
        this.resourceService = resourceService;
        this.userFileService = userFileService;
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

    public UserDb updateUser(Principal userPrincipal, UpdateUserRequest request, MultiValueMap<String, MultipartFile> files) throws ResourceNotFoundException {
        UserDb userDb = loadUserByUsername(userPrincipal.getName());
        if (userDb == null) {
            throw new ResourceNotFoundException("Could not find user for '%s' username".formatted(userPrincipal.getName()));
        }
        if (request != null) {
            BeanUtils.copyProperties(request, userDb);// TODO here additional checks is needed!!!
        }
        if (files != null && !files.isEmpty()) {
            updateUserFiles(files, userDb);
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

    public void updateUserFiles(MultiValueMap<String, MultipartFile> files, UserDb userDb) {
        List<UserFileDb> userFileDbListToSave = new ArrayList<>();

        files.forEach(
                (key, multipartFiles) -> {
                    List<UserFileDb> userFileDbList = userFileService.findUserIdAndKey(userDb.getId(), key);
                    if (!userFileDbList.isEmpty()) {
                        userFileService.deleteAll(userFileDbList);
                    }
                    userFileDbListToSave.addAll(getNewUserFileDbList(key, multipartFiles));
                }
        );
        userFileDbListToSave.forEach(userFileDb -> userFileDb.setUserId(userDb.getId()));
        userFileService.saveAll(userFileDbListToSave);
    }

    private List<UserFileDb> getNewUserFileDbList(String key, List<MultipartFile> multipartFileList) {
        List<UserFileDb> userFileDbListToSave = new ArrayList<>();
        multipartFileList.forEach(file -> {
            try {
                ObjectId fileId = resourceService.uploadFile(file);
                userFileDbListToSave.add(new UserFileDb(fileId.toString(), key));
            } catch (IOException e) {
                LOGGER.warn("Error while uploading file to database.");
            }
        });
        return userFileDbListToSave;
    }


    public List<UserResponse> getMentorsAndTutors() {
        Set<UserDb> result = new HashSet<>(repository.findAllByProgramRolesContains(UserProgramRole.MENTOR));
        result.addAll(repository.findAllByProgramRolesContains(UserProgramRole.TUTOR));

        return result.stream().map(this::toUserResponse).toList();
    }

    public UserResponse toUserResponse(UserDb userDb) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userDb, userResponse);
        addAllUserFilesToUserResponse(userResponse);
        return userResponse;
    }

    public void addAllUserFilesToUserResponse(UserResponse userResponse) {
        List<UserFileDb> userFileDbList = userFileService.findAllByUserId(userResponse.getId());
        userFileDbList.forEach(userFileDb -> userResponse.getKeyToFileIdsMap().add(userFileDb.getKey(), userFileDb.getFileId()));
    }

}
