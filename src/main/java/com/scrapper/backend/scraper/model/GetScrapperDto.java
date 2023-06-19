package com.scrapper.backend.scraper.model;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GetScrapperDto {

    private String id;
    private StatusScrapper status;
    private ConcurrentLinkedQueue<String> urls;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status.getDescription();
    }

    public void setStatus(StatusScrapper status) {
        this.status = status;
    }

    public ConcurrentLinkedQueue<String> getUrls() {
        return urls;
    }

    public void setUrls(ConcurrentLinkedQueue<String> urls) {
        this.urls = urls;
    }
}
