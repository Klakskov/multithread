package com.scrapperapp.backend.scraper.service.interfaces;

import com.scrapperapp.backend.scraper.model.ScrapperEntity;

import java.util.ArrayList;

public interface IDoScrapper {




    void doScrapper(ScrapperEntity entity, String url,
                    String root,
                    ArrayList<String> urlVisited);
}
