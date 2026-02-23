package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.NewsDb;
import ge.batumi.tutormentor.model.db.NewsFileDb;
import ge.batumi.tutormentor.model.request.NewsRequest;
import ge.batumi.tutormentor.model.response.NewsResponse;
import ge.batumi.tutormentor.repository.NewsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing news articles and their associated files.
 */
@Service
public class NewsService extends ARepositoryService<NewsRepository, NewsDb, String> {
    private static final Logger LOGGER = LogManager.getLogger(NewsService.class);
    private final NewsFilesService newsFilesService;
    private final FileUploadHelper fileUploadHelper;

    public NewsService(NewsRepository repository, NewsFilesService newsFilesService, FileUploadHelper fileUploadHelper) {
        super(repository);
        this.newsFilesService = newsFilesService;
        this.fileUploadHelper = fileUploadHelper;
    }

    /**
     * Creates a news article and optionally attaches files to it.
     *
     * @param request the news data.
     * @param files   optional files to associate with the news article.
     * @return the persisted {@link NewsDb} entity.
     */
    @Transactional // requires MongoDB replica set
    public NewsDb addNews(NewsRequest request, MultiValueMap<String, MultipartFile> files) {
        NewsDb newsDb = new NewsDb(request);
        newsDb = repository.save(newsDb);
        if (files != null && !files.isEmpty()) {
            addFilesToNewsDb(files, newsDb);
        }

        return newsDb;
    }

    private void addFilesToNewsDb(MultiValueMap<String, MultipartFile> files, NewsDb newsDb) {
        fileUploadHelper.uploadFiles(
                files,
                NewsFileDb::new,
                fileDb -> fileDb.setNewsId(newsDb.getId()),
                null,
                newsFilesService::saveAll
        );
    }

    /**
     * Converts a {@link NewsDb} entity into a {@link NewsResponse} DTO, including file mappings.
     */
    public NewsResponse getAsNewsResponse(NewsDb newsDb) {
        NewsResponse newsResponse = NewsResponse.builder()
                .id(newsDb.getId())
                .title(newsDb.getTitle())
                .description(newsDb.getDescription())
                .addDate(newsDb.getAddDate())
                .build();
        addAllNewsFilesToNewsResponse(newsResponse);
        return newsResponse;
    }

    /**
     * Populates file ID mappings on an existing {@link NewsResponse}.
     */
    public void addAllNewsFilesToNewsResponse(NewsResponse newsResponse) {
        List<NewsFileDb> newsFileDbList = newsFilesService.findAllByNewsId(newsResponse.getId());
        newsFileDbList.forEach(newsFileDb -> {
            if (newsResponse.getKeyToFileIdsMap() == null) {
                newsResponse.setKeyToFileIdsMap(new LinkedMultiValueMap<>());
            }

            newsResponse.getKeyToFileIdsMap().add(newsFileDb.getKey(), newsFileDb.getFileId());
        });
    }

    /**
     * Deletes a news article and all of its associated files.
     *
     * @param id the news article ID.
     * @return {@code true} if the article existed and was deleted, {@code false} otherwise.
     */
    @Transactional // requires MongoDB replica set
    public boolean deleteNews(String id) throws NotFound {
        Optional<NewsDb> newsDbOptional = repository.findById(id);

        if (newsDbOptional.isEmpty()) {
            return false;
        }
        List<NewsFileDb> newsFileDbListToDelete = newsFilesService.findAllByNewsId(id);
        newsFilesService.deleteAll(newsFileDbListToDelete);
        repository.deleteById(id);

        return true;
    }

    /**
     * Converts a list of {@link NewsDb} entities to {@link NewsResponse} DTOs.
     */
    public List<NewsResponse> getAllAsNewsResponse(List<NewsDb> newsDbList) {
        return newsDbList.stream().map(this::getAsNewsResponse).toList();
    }

    /**
     * Returns all news articles as response DTOs.
     */
    public List<NewsResponse> getAllAsNewsResponse() {
        return getAllAsNewsResponse(findAll());
    }

    /**
     * Returns a paginated view of all news articles as response DTOs.
     */
    public Page<NewsResponse> getAllAsNewsResponse(Pageable pageable) {
        return findAll(pageable).map(this::getAsNewsResponse);
    }
}
