package com.scrapperapp.backend.scraper.service;

import com.scrapperapp.backend.request.service.IRequestService;
import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.model.ScrapperEntity;
import com.scrapperapp.backend.scraper.model.ScrapperWorker;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.scrapperapp.backend.scraper.model.StatusScrapper.ACTIVE;
import static com.scrapperapp.backend.utils.UtilFunctions.createId;

public class CreateScrapperService implements ICreateScrapperService {

    private static Logger log = LoggerFactory.getLogger(CreateScrapperService.class);

    private final Database database;
    private final String baseUrl;

    private final IDoScrapper doScrapper;

    private final IRequestService requestService;

    public CreateScrapperService(Database database, String baseUrl,
                                 IDoScrapper doScrapper, IRequestService requestService) {
        this.database = database;
        this.baseUrl = baseUrl;
        this.doScrapper = doScrapper;
        this.requestService = requestService;
    }

    @Override
    public PostScrapperDto createScrapper(String keyword) {
        final String id = createId();

        log.info("creating scrapper for keyword {} with id {} ",
                keyword, id);
        ScrapperEntity entity = createEntityScrapper(id, keyword );
        database.saveData(entity);

        ScrapperWorker newWorker = new ScrapperWorker(
                entity, baseUrl, baseUrl,
                new Vector<String>(), requestService,
                doScrapper, database
        );


        doScrapper.doScrapper(newWorker);


        return createPostScrapperDto(id);
    }


    private PostScrapperDto createPostScrapperDto(String id){
        PostScrapperDto postScrapperDto = new PostScrapperDto();
        postScrapperDto.setId(id);

        return  postScrapperDto;
    }

    private ScrapperEntity createEntityScrapper(String id, String keyword) {
        ScrapperEntity scrapperEntity = new ScrapperEntity();
        scrapperEntity.setId(id);
        scrapperEntity.setKeyword(keyword);
        scrapperEntity.setStatus(ACTIVE);
        scrapperEntity.setUrls(new ConcurrentLinkedQueue<>());
        scrapperEntity.setCountUrlInAction(new AtomicInteger(1));

        return  scrapperEntity;

    }
}
