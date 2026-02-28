package ge.batumi.tutormentor.controller.admin;

import ge.batumi.tutormentor.model.request.NewsRequest;
import ge.batumi.tutormentor.model.response.NewsResponse;
import ge.batumi.tutormentor.services.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin REST controller for creating and deleting news articles.
 */
@RestController
@RequestMapping("v1/admin/news")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class NewsAdminController {
    private final NewsService newsService;

    /**
     * Creates a new news article with optional file attachments.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NewsResponse> addNews(@Valid @RequestPart("data") NewsRequest request,
                                                @RequestParam MultiValueMap<String, MultipartFile> files) {
        return ResponseEntity.ok(newsService.getAsNewsResponse(newsService.addNews(request, files)));
    }

    /**
     * Deletes a news article and its associated files by ID.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteNews(@PathVariable("id") String id) {
        return ResponseEntity.ok(newsService.deleteNews(id));
    }
}
