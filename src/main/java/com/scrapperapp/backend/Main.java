package com.scrapperapp.backend;

import com.google.gson.Gson;
import com.scrapperapp.backend.scraper.controller.IScrapperController;
import com.scrapperapp.backend.scraper.controller.ScrapperController;
import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.fakeDatabase.FakeDatabase;
import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.InputPostScrapper;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.service.CreateScrapperService;
import com.scrapperapp.backend.scraper.service.GetScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.IGetScrapperService;

import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

public class Main {
    private static IScrapperController scrapperController;
    private static Gson gson;

    public static void main(String[] args) {



        initialize();
        get("/crawl/:id", (req, res) ->{
            //"GET /crawl/" + req.params("id")
            String id = req.params("id");
            if(id == null){
                res.status(400);
                res.body("Id nao informado!");
                return gson.toJson("Error: \"Id nao informado!\" ");
            }

            GetScrapperDto dto = scrapperController.getScrapperById(id);

            res.status(200);
            res.body(gson.toJson(dto));
            return  gson.toJson(dto);

                }
               );
        post("/crawl", (req, res) -> {
            //"POST /crawl" + System.lineSeparator() + req.body()
            res.type("application/json");
            String body = req.body();
            if(body == null){
                res.status(400);
                res.body("Nao foi enviado keyword para busca!");
                return res;
            }
            InputPostScrapper input = gson.fromJson(body, InputPostScrapper.class );
            PostScrapperDto dto = scrapperController.createScrapper(input);


            res.status(201);
            res.body(gson.toJson(dto));
            return gson.toJson(dto);

                }
        );

        get("/hello", (req, res)->"Hello, world");
    }


    private static void initialize(){
        Database fakeDatabase = new FakeDatabase(new ConcurrentHashMap<>());
        ICreateScrapperService createScrapperService = new CreateScrapperService(fakeDatabase);
        IGetScrapperService getScrapperService = new GetScrapperService(fakeDatabase);

        scrapperController = new ScrapperController(createScrapperService, getScrapperService);
        gson = new Gson();

    }
}
