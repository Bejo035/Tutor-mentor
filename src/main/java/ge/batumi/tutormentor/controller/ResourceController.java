package ge.batumi.tutormentor.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import ge.batumi.tutormentor.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for serving uploaded file resources from GridFS.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/resource")
@CrossOrigin(origins = "*")
public class ResourceController {
    private static final String CONTENT_TYPE_KEY = "_contentType";
    private final ResourceService resourceService;

    /**
     * Returns a file resource by its GridFS ID with the appropriate content type.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResource(@PathVariable String id) {

        GridFSFile file = resourceService.findById(id);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = resourceService.getResource(file);

        Document metadata = file.getMetadata();
        String contentType = (metadata != null) ? metadata.getString(CONTENT_TYPE_KEY) : null;
        MediaType mediaType = (contentType != null)
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }

}

