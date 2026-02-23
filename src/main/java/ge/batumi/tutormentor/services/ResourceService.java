package ge.batumi.tutormentor.services;


import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final GridFsTemplate gridFsTemplate;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    );

    private static final Map<String, Set<String>> EXTENSION_TO_CONTENT_TYPES = Map.ofEntries(
            Map.entry(".jpg", Set.of("image/jpeg")),
            Map.entry(".jpeg", Set.of("image/jpeg")),
            Map.entry(".png", Set.of("image/png")),
            Map.entry(".gif", Set.of("image/gif")),
            Map.entry(".webp", Set.of("image/webp")),
            Map.entry(".pdf", Set.of("application/pdf")),
            Map.entry(".doc", Set.of("application/msword")),
            Map.entry(".docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
            Map.entry(".xls", Set.of("application/vnd.ms-excel")),
            Map.entry(".xlsx", Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            Map.entry(".ppt", Set.of("application/vnd.ms-powerpoint")),
            Map.entry(".pptx", Set.of("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
    );

    public ObjectId uploadFile(MultipartFile file) throws IOException {
        validateFile(file);
        return gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
    }

    private void validateFile(MultipartFile file) throws BadRequestException {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!EXTENSION_TO_CONTENT_TYPES.containsKey(extension)) {
                throw new BadRequestException("File extension not allowed: " + extension);
            }
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot);
    }

    public GridFSFile findById(String id) {
        return gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(id))
        );
    }

    public GridFsResource getResource(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }

    public void deleteResourceById(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }
}
