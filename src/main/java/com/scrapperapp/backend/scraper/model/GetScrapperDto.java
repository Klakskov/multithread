package com.scrapperapp.backend.scraper.model;

import java.util.ArrayList;

public class GetScrapperDto {

    private String id;
    private StatusScrapper status;
    private ArrayList<String> urls;


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

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }
}
