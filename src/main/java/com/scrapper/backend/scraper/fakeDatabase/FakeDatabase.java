package com.scrapper.backend.scraper.fakeDatabase;

import com.scrapper.backend.scraper.model.ScrapperEntity;

import java.util.concurrent.ConcurrentHashMap;

public class FakeDatabase implements Database{

    private final ConcurrentHashMap<String, ScrapperEntity> database;

    public FakeDatabase(ConcurrentHashMap<String, ScrapperEntity> database) {
        this.database = database;
    }

    @Override
    public ScrapperEntity saveData( ScrapperEntity data) {
        database.put(data.getId(), data);
        return data;

    }

    @Override
    public ScrapperEntity updateData( ScrapperEntity data) {
        database.replace(data.getId(), data);
        return data;
    }


    @Override
    public ScrapperEntity getDtaById(String id) {
        return database.get(id);
    }
}
