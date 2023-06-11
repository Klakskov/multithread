package com.scrapperapp.backend.scraper.model;

public enum StatusScrapper {

    ACTIVE("active"),
    DONE("done");

    private String description;

    public String getDescription() {
        return description;
    }

    private StatusScrapper(String description) {
        this.description = description;
    }
}
