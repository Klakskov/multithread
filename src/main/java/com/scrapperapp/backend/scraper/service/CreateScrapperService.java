package com.scrapperapp.backend.scraper.service;

import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.model.ScrapperEntity;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;

import java.util.ArrayList;

import static com.scrapperapp.backend.scraper.model.StatusScrapper.ACTIVE;
import static com.scrapperapp.backend.utils.UtilFunctions.createId;

public class CreateScrapperService implements ICreateScrapperService {

    private final Database database;

    public CreateScrapperService(Database database) {
        this.database = database;
    }

    @Override
    public PostScrapperDto createScrapper(String keyword) {
        final String id = createId();

        ScrapperEntity entity = createEntityScrapper(id, keyword );
        database.saveData(id, entity);

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
        scrapperEntity.setUrls(new ArrayList<>());

        return  scrapperEntity;

    }
}
