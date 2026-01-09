package ge.batumi.tutormentor.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class ImageController {
    private final GridFsTemplate gridFsTemplate;

    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String id) {

        GridFSFile file = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(id))
        );

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsTemplate.getResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        file.getMetadata().getString("_contentType")
                ))
                .body(resource);
    }

}

