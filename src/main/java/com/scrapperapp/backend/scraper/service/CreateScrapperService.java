package com.scrapperapp.backend.scraper.service;

import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.model.ScrapperEntity;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.scrapperapp.backend.scraper.model.StatusScrapper.ACTIVE;
import static com.scrapperapp.backend.utils.UtilFunctions.createId;

public class CreateScrapperService implements ICreateScrapperService {

    private static Logger log = LoggerFactory.getLogger(CreateScrapperService.class);

    private final Database database;
    private final String baseUrl;

    public CreateScrapperService(Database database, String baseUrl) {
        this.database = database;
        this.baseUrl = baseUrl;
    }

    @Override
    public PostScrapperDto createScrapper(String keyword) {
        final String id = createId();

        log.info("creating scrapper for keyword {} with id {} ",
                keyword, id);
        ScrapperEntity entity = createEntityScrapper(id, keyword );
        database.saveData(entity);

        Thread newThread = new Thread(() -> {
            IDoScrapper doScrapper = new DoScrapper(database);
            doScrapper.doScrapper(entity, baseUrl, baseUrl, new ArrayList<>());
        });
        newThread.start();

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
        scrapperEntity.setUrlToVisite(new AtomicInteger(0));

        return  scrapperEntity;

    }
}
