package ge.batumi.tutormentor.controller;

import ge.batumi.tutormentor.model.response.NewsResponse;
import ge.batumi.tutormentor.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public REST controller for retrieving news articles.
 */
@RestController
@RequestMapping("api/v1/news")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    /**
     * Returns a paginated list of news articles.
     */
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(newsService.getAllAsNewsResponse(PageRequest.of(page, size)));
    }
}
