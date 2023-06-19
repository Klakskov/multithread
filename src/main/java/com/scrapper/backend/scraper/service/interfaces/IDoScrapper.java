package com.scrapper.backend.scraper.service.interfaces;

import com.scrapper.backend.request.service.IRequestService;
import com.scrapper.backend.scraper.model.ScrapperEntity;
import com.scrapper.backend.scraper.fakeDatabase.Database;

import java.util.Vector;

public interface IDoScrapper {


    void sendToProccess(
            ScrapperEntity entity,
            String urlToProccess,
            String rootUrl,
            Vector<String> urlVisited,
            IRequestService requestService,
            IDoScrapper doScrapper,
            Database database);
}
