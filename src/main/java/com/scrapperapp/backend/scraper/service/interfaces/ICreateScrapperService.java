package com.scrapperapp.backend.scraper.service.interfaces;

import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;

public interface ICreateScrapperService {

    PostScrapperDto createScrapper(String term);

}
