package com.scrapperapp.backend.scraper.controller;

import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.InputPostScrapper;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;

public interface IScrapperController {

    PostScrapperDto createScrapper(InputPostScrapper input);

    GetScrapperDto getScrapperById(String id);


}
