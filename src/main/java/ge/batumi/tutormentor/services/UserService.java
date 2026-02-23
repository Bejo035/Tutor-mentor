package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.exceptions.ResourceNotFoundException;
import ge.batumi.tutormentor.model.db.UserDb;
import ge.batumi.tutormentor.model.db.UserFileDb;
import ge.batumi.tutormentor.model.db.UserProgramRole;
import ge.batumi.tutormentor.model.request.UpdateUserRequest;
import ge.batumi.tutormentor.model.request.UserRequest;
import ge.batumi.tutormentor.model.response.UserPublicResponse;
import ge.batumi.tutormentor.model.response.UserResponse;
import ge.batumi.tutormentor.repository.UserRepository;
import ge.batumi.tutormentor.security.UserDetailsAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.List;

/**
 * Service layer for user management including CRUD, file uploads, and Spring Security integration.
 */
@Service
public class UserService extends ARepositoryService<UserRepository, UserDb, String> implements UserDetailsService {
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    private final UserFileService userFileService;
    private final FileUploadHelper fileUploadHelper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, UserFileService userFileService, FileUploadHelper fileUploadHelper, PasswordEncoder passwordEncoder) {
        super(repository);
        this.userFileService = userFileService;
        this.fileUploadHelper = fileUploadHelper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Persists the given user entity and returns the saved instance.
     */
    public UserDb save(UserDb userDb) {
        return repository.save(userDb);
    }

    /**
     * Creates a new user from the given request, encoding the password.
     */
    public UserDb addUser(UserRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return repository.save(new UserDb(request));
    }

    /**
     * Updates an existing user by ID (admin use).
     *
     * @param id      the user ID.
     * @param request the updated user data.
     * @return the updated user.
     * @throws ResourceNotFoundException if no user exists for the given ID.
     */
    public UserDb updateUser(String id, UserRequest request) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        if (request.getPassword() != null) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        BeanUtils.copyProperties(request, userDb);
        return repository.save(userDb);
    }

    /**
     * Updates the authenticated user's profile and optionally replaces associated files.
     *
     * @param userPrincipal the authenticated user principal.
     * @param request       the updated profile data, or {@code null} to skip.
     * @param files         optional files to upload/replace.
     * @return the updated user.
     * @throws ResourceNotFoundException if the user cannot be found by username.
     */
    @Transactional // requires MongoDB replica set
    public UserDb updateUser(Principal userPrincipal, UpdateUserRequest request, MultiValueMap<String, MultipartFile> files) throws ResourceNotFoundException {
        UserDb userDb = findByUsername(userPrincipal.getName());
        if (userDb == null) {
            throw new ResourceNotFoundException("Could not find user for '%s' username".formatted(userPrincipal.getName()));
        }
        if (request != null) {
            BeanUtils.copyProperties(request, userDb);
        }
        if (files != null && !files.isEmpty()) {
            updateUserFiles(files, userDb);
        }

        return repository.save(userDb);
    }

    /**
     * Returns all users matching the given IDs.
     */
    public List<UserDb> findAllById(List<String> userIds) {
        return repository.findAllById(userIds);
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    /**
     * Checks whether a user with the given email already exists.
     */
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Checks whether a user with the given username already exists.
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Looks up a user entity by username (for internal use).
     *
     * @param username the username.
     * @return the {@link UserDb}, or {@code null} if not found.
     */
    public UserDb findByUsername(String username) {
        return repository.findByUsername(username);
    }

    /**
     * {@inheritDoc} Loads a {@link UserDetailsAdapter} for Spring Security authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDb userDb = repository.findByUsername(username);
        if (userDb == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return new UserDetailsAdapter(userDb);
    }

    /**
     * Confirms a user account.
     *
     * @param id the user ID.
     * @return {@code true} if the user was confirmed, {@code false} if already confirmed.
     * @throws ResourceNotFoundException if no user exists for the given ID.
     */
    public boolean confirmUser(String id) throws ResourceNotFoundException {
        UserDb userDb = findById(id);
        if (userDb.isConfirmed()) {
            return false;
        }
        userDb.setConfirmed(true);
        repository.save(userDb);
        return true;
    }

    /**
     * Uploads and associates files with a user, replacing any existing files for the same keys.
     *
     * @param files  the multipart files grouped by key.
     * @param userDb the user to associate files with.
     */
    public void updateUserFiles(MultiValueMap<String, MultipartFile> files, UserDb userDb) {
        fileUploadHelper.uploadFiles(
                files,
                UserFileDb::new,
                fileDb -> fileDb.setUserId(userDb.getId()),
                (key, _k) -> {
                    List<UserFileDb> existing = userFileService.findByUserIdAndKey(userDb.getId(), key);
                    if (!existing.isEmpty()) {
                        userFileService.deleteAll(existing);
                    }
                },
                userFileService::saveAll
        );
    }

    /**
     * Returns paginated public profile data for all users who have a MENTOR or TUTOR program role.
     */
    public Page<UserPublicResponse> getMentorsAndTutorsPublic(Pageable pageable) {
        Page<UserDb> page = repository.findByProgramRolesIn(
                List.of(UserProgramRole.MENTOR, UserProgramRole.TUTOR), pageable);
        return page.map(this::toUserPublicResponse);
    }

    /**
     * Converts a {@link UserDb} entity to a {@link UserResponse} DTO, including file mappings.
     */
    public UserResponse toUserResponse(UserDb userDb) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userDb, userResponse);
        addAllUserFilesToUserResponse(userResponse);
        return userResponse;
    }

    /**
     * Converts a {@link UserDb} entity to a public-facing {@link UserPublicResponse} DTO.
     */
    public UserPublicResponse toUserPublicResponse(UserDb userDb) {
        UserPublicResponse response = new UserPublicResponse();
        response.setId(userDb.getId());
        response.setName(userDb.getName());
        response.setSurname(userDb.getSurname());
        response.setProgramRoles(userDb.getProgramRoles());
        response.setMentoringCourseName(userDb.getMentoringCourseName());
        response.setCourseDescription(userDb.getCourseDescription());
        response.setRating(userDb.getRating());

        List<UserFileDb> userFileDbList = userFileService.findAllByUserId(userDb.getId());
        MultiValueMap<String, String> keyToFileIdsMap = new LinkedMultiValueMap<>();
        userFileDbList.forEach(userFileDb -> keyToFileIdsMap.add(userFileDb.getKey(), userFileDb.getFileId()));
        response.setKeyToFileIdsMap(keyToFileIdsMap);

        return response;
    }

    /**
     * Populates file ID mappings on an existing {@link UserResponse}.
     */
    public void addAllUserFilesToUserResponse(UserResponse userResponse) {
        List<UserFileDb> userFileDbList = userFileService.findAllByUserId(userResponse.getId());
        userFileDbList.forEach(userFileDb -> {
            if (userResponse.getKeyToFileIdsMap() == null) {
                userResponse.setKeyToFileIdsMap(new LinkedMultiValueMap<>());
            }
            userResponse.getKeyToFileIdsMap().add(userFileDb.getKey(), userFileDb.getFileId());
        });
    }

}
