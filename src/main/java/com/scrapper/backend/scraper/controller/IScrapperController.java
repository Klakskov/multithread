package com.scrapper.backend.scraper.controller;

import com.scrapper.backend.scraper.model.PostScrapperDto;
import com.scrapper.backend.scraper.model.GetScrapperDto;
import com.scrapper.backend.scraper.model.InputPostScrapper;

public interface IScrapperController {

    PostScrapperDto createScrapper(InputPostScrapper input);

    GetScrapperDto getScrapperById(String id);


}
