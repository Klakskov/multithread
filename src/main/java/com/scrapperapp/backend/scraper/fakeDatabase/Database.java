package com.scrapperapp.backend.scraper.fakeDatabase;

import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.ScrapperEntity;

public interface Database {

        String saveData(String term, ScrapperEntity data);

        String updateData(String id, ScrapperEntity data);

        ScrapperEntity getDtaById(String id);
}
