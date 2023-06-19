package com.scrapper.backend.scraper.service.interfaces;

import com.scrapper.backend.scraper.model.GetScrapperDto;

public interface IGetScrapperService {
    GetScrapperDto getScrapper(String id);

}
