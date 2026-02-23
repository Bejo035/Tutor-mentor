package ge.batumi.tutormentor.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Reusable helper that encapsulates the common file upload pattern:
 * iterate multipart files, upload each to GridFS, create a file-DB entity, and batch-save.
 */
@Component
public class FileUploadHelper {
    private static final Logger LOGGER = LogManager.getLogger(FileUploadHelper.class);
    private final ResourceService resourceService;

    public FileUploadHelper(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * Generic file upload method that handles the common pattern of uploading files,
     * creating file DB entities, optionally deleting existing files, and saving.
     *
     * @param files           the multipart files grouped by key
     * @param fileDbFactory   creates a new file DB entity from (fileId, key)
     * @param ownerSetter     sets the owner ID on each file DB entity
     * @param existingDeleter optional: deletes existing files for (ownerId, key); pass null to skip
     * @param batchSaver      saves all created file DB entities
     * @param <F>             the file DB entity type
     */
    public <F> void uploadFiles(
            MultiValueMap<String, MultipartFile> files,
            BiFunction<String, String, F> fileDbFactory,
            Consumer<F> ownerSetter,
            BiConsumer<String, String> existingDeleter,
            Consumer<List<F>> batchSaver
    ) {
        List<F> toSave = new ArrayList<>();

        files.forEach((key, multipartFiles) -> {
            if (existingDeleter != null) {
                existingDeleter.accept(key, key);
            }
            multipartFiles.forEach(file -> {
                try {
                    ObjectId fileId = resourceService.uploadFile(file);
                    F entity = fileDbFactory.apply(fileId.toString(), key);
                    ownerSetter.accept(entity);
                    toSave.add(entity);
                } catch (IOException e) {
                    LOGGER.warn("Error while uploading file to database.");
                }
            });
        });

        batchSaver.accept(toSave);
    }
}
