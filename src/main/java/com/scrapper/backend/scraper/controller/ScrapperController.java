package com.scrapper.backend.scraper.controller;

import com.scrapper.backend.scraper.model.GetScrapperDto;
import com.scrapper.backend.scraper.model.InputPostScrapper;
import com.scrapper.backend.scraper.model.PostScrapperDto;
import com.scrapper.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapper.backend.scraper.service.interfaces.IGetScrapperService;

public class ScrapperController implements IScrapperController{

    private final ICreateScrapperService createScrapperService;
    private final IGetScrapperService getScrapperService;

    public ScrapperController(ICreateScrapperService createScrapperService,
                              IGetScrapperService getScrapperService) {
        this.createScrapperService = createScrapperService;
        this.getScrapperService = getScrapperService;
    }

    @Override
    public PostScrapperDto createScrapper(InputPostScrapper input) {
        return createScrapperService.createScrapper(input.getKeyword());
    }

    @Override
    public GetScrapperDto getScrapperById(String id) {
        return getScrapperService.getScrapper(id);
    }
}
