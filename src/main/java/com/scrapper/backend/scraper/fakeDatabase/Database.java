package com.scrapper.backend.scraper.fakeDatabase;

import com.scrapper.backend.scraper.model.ScrapperEntity;

public interface Database {

        ScrapperEntity saveData(ScrapperEntity data);

        ScrapperEntity updateData( ScrapperEntity data);

        ScrapperEntity getDtaById(String id);
}
