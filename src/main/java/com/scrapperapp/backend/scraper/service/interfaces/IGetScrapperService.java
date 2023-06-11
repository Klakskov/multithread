package com.scrapperapp.backend.scraper.service.interfaces;

import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;

public interface IGetScrapperService {
    GetScrapperDto getScrapper(String id);

}
