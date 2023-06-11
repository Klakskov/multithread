package com.scrapperapp.backend.scraper.controller;

import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.InputPostScrapper;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.IGetScrapperService;

public class ScrapperController implements IScrapperController{

    private final ICreateScrapperService createScrapperService;
    private final IGetScrapperService getScrapperService;

    public ScrapperController(ICreateScrapperService createScrapperService,
                              IGetScrapperService getScrapperService) {
        this.createScrapperService = createScrapperService;
        this.getScrapperService = getScrapperService;
    }

    @Override
    public PostScrapperDto createScrapper( InputPostScrapper input) {
        return createScrapperService.createScrapper(input.getKeyword());
    }

    @Override
    public GetScrapperDto getScrapperById(String id) {
        return getScrapperService.getScrapper(id);
    }
}
