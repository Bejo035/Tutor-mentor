package ge.batumi.tutormentor.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import ge.batumi.tutormentor.services.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for serving uploaded file resources from GridFS.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/resource")
public class ResourceController {
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

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        file.getMetadata().getString("_contentType")
                ))
                .body(resource);
    }

}

