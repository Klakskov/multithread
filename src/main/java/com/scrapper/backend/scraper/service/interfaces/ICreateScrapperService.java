package com.scrapper.backend.scraper.service.interfaces;

import com.scrapper.backend.scraper.model.PostScrapperDto;

public interface ICreateScrapperService {

    PostScrapperDto createScrapper(String term);

}
