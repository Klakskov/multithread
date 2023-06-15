package com.scrapperapp.backend.scraper.fakeDatabase;

import com.scrapperapp.backend.scraper.model.ScrapperEntity;

public interface Database {

        ScrapperEntity saveData(ScrapperEntity data);

        ScrapperEntity updateData( ScrapperEntity data);

        ScrapperEntity getDtaById(String id);
}
