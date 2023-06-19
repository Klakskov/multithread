package com.scrapper.backend.scraper.service;

import com.scrapper.backend.request.service.IRequestService;
import com.scrapper.backend.scraper.model.PostScrapperDto;
import com.scrapper.backend.scraper.model.ScrapperEntity;
import com.scrapper.backend.scraper.model.StatusScrapper;
import com.scrapper.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapper.backend.scraper.service.interfaces.IDoScrapper;
import com.scrapper.backend.utils.UtilFunctions;
import com.scrapper.backend.scraper.fakeDatabase.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

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
        final String id = UtilFunctions.createId();

        log.info("creating scrapper for keyword {} with id {} ",
                keyword, id);
        ScrapperEntity entity = createEntityScrapper(id, keyword );
        database.saveData(entity);

        doScrapper.sendToProccess(entity,
                baseUrl,
                baseUrl,
                new Vector<String>(),
                requestService,
                doScrapper,
                database);


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
        scrapperEntity.setStatus(StatusScrapper.ACTIVE);
        scrapperEntity.setUrls(new ConcurrentLinkedQueue<>());
        scrapperEntity.setCountUrlInAction(new AtomicInteger(0));
        return  scrapperEntity;

    }
}
