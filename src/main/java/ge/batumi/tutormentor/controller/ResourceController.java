package ge.batumi.tutormentor.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import ge.batumi.tutormentor.services.ResourceService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/resource")
@PermitAll
public class ResourceController {
    private final ResourceService resourceService;

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

