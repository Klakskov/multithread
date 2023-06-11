package com.scrapperapp.backend.scraper.fakeDatabase;

import com.scrapperapp.backend.scraper.model.ScrapperEntity;

import java.util.concurrent.ConcurrentMap;

public class FakeDatabase implements Database{

    private final ConcurrentMap<String, ScrapperEntity> database;

    public FakeDatabase(ConcurrentMap<String, ScrapperEntity> database) {
        this.database = database;
    }

    @Override
    public String saveData(String id, ScrapperEntity data) {
        database.put(id, data);
        return id;

    }

    @Override
    public String updateData(String id, ScrapperEntity data) {
        database.replace(id, data);
        return id;
    }


    @Override
    public ScrapperEntity getDtaById(String id) {
        return database.get(id);
    }
}
