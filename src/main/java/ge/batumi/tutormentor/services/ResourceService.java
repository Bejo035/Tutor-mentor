package ge.batumi.tutormentor.services;


import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final GridFsTemplate gridFsTemplate;

    public ObjectId uploadFile(MultipartFile file) throws IOException {
        return gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
    }

    public GridFSFile findById(String id) {
        return gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(id))
        );
    }

    public GridFsResource getResource(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }
}
