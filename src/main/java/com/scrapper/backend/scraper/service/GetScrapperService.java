package com.scrapper.backend.scraper.service;

import com.scrapper.backend.scraper.fakeDatabase.Database;
import com.scrapper.backend.scraper.model.GetScrapperDto;
import com.scrapper.backend.scraper.model.ScrapperEntity;
import com.scrapper.backend.scraper.service.interfaces.IGetScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetScrapperService implements IGetScrapperService {

    private static Logger log = LoggerFactory.getLogger(GetScrapperService.class);

    private final Database database;

    public GetScrapperService(Database database) {
        this.database = database;
    }

    @Override
    public GetScrapperDto getScrapper(String id) {
        ScrapperEntity entity = database.getDtaById(id);

        if(entity == null) {
            log.info("entity not found for id {} ", id);
            return null;
        }
        return createGetScrapper(entity);

    }

    private GetScrapperDto createGetScrapper(ScrapperEntity entity) {
        GetScrapperDto getScrapperDto = new GetScrapperDto();

        getScrapperDto.setId(entity.getId());
        getScrapperDto.setStatus(entity.getStatus());
        getScrapperDto.setUrls(entity.getUrls());

        return getScrapperDto;
    }

}
