package ge.batumi.tutormentor.services;

import ge.batumi.tutormentor.model.db.NewsDb;
import ge.batumi.tutormentor.model.db.NewsFileDb;
import ge.batumi.tutormentor.model.request.NewsRequest;
import ge.batumi.tutormentor.model.response.NewsResponse;
import ge.batumi.tutormentor.repository.NewsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NewsService extends ARepositoryService<NewsRepository, NewsDb, String> {
    private static final Logger LOGGER = LogManager.getLogger(NewsService.class);
    private final NewsFilesService newsFilesService;
    private final ResourceService resourceService;

    public NewsService(NewsRepository repository, NewsFilesService newsFilesService, ResourceService resourceService) {
        super(repository);
        this.newsFilesService = newsFilesService;
        this.resourceService = resourceService;
    }

    public NewsDb addNews(NewsRequest request, MultiValueMap<String, MultipartFile> files) {
        NewsDb newsDb = new NewsDb(request);
        newsDb = repository.save(newsDb);
        if (files != null && !files.isEmpty()) {
            addFilesToNewsDb(files, newsDb);
        }

        return newsDb;
    }

    private void addFilesToNewsDb(MultiValueMap<String, MultipartFile> files, NewsDb newsDb) {
        List<NewsFileDb> newsFileDbListToSave = new ArrayList<>();

        files.forEach((key, multipartFiles) -> newsFileDbListToSave.addAll(getNewNewsFileDbList(key, multipartFiles)));
        newsFileDbListToSave.forEach(newsFileDb -> newsFileDb.setNewsId(newsDb.getId()));
        newsFilesService.saveAll(newsFileDbListToSave);
    }


    private List<NewsFileDb> getNewNewsFileDbList(String key, List<MultipartFile> multipartFileList) {
        List<NewsFileDb> newsDbList = new ArrayList<>();
        multipartFileList.forEach(file -> {
            try {
                ObjectId fileId = resourceService.uploadFile(file);
                newsDbList.add(new NewsFileDb(fileId.toString(), key));
            } catch (IOException e) {
                LOGGER.warn("Error while uploading file to database.");
            }
        });
        return newsDbList;
    }

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

    public void addAllNewsFilesToNewsResponse(NewsResponse newsResponse) {
        List<NewsFileDb> newsFileDbList = newsFilesService.findAllByNewsId(newsResponse.getId());
        newsFileDbList.forEach(newsFileDb -> {
            if (newsResponse.getKeyToFileIdsMap() == null) {
                newsResponse.setKeyToFileIdsMap(new LinkedMultiValueMap<>());
            }

            newsResponse.getKeyToFileIdsMap().add(newsFileDb.getKey(), newsFileDb.getFileId());
        });
    }

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

    public List<NewsResponse> getAllAsNewsResponse(List<NewsDb> newsDbList) {
        return newsDbList.stream().map(this::getAsNewsResponse).toList();
    }

    public List<NewsResponse> getAllAsNewsResponse() {
        return getAllAsNewsResponse(findAll());
    }
}
