package com.scrapperapp.backend;

import com.google.gson.Gson;
import com.scrapperapp.backend.request.service.IRequestService;
import com.scrapperapp.backend.request.service.RequestService;
import com.scrapperapp.backend.scraper.controller.IScrapperController;
import com.scrapperapp.backend.scraper.controller.ScrapperController;
import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.fakeDatabase.FakeDatabase;
import com.scrapperapp.backend.scraper.model.GetScrapperDto;
import com.scrapperapp.backend.scraper.model.InputPostScrapper;
import com.scrapperapp.backend.scraper.model.PostScrapperDto;
import com.scrapperapp.backend.scraper.service.CreateScrapperService;
import com.scrapperapp.backend.scraper.service.DoScrapper;
import com.scrapperapp.backend.scraper.service.GetScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import com.scrapperapp.backend.scraper.service.interfaces.IGetScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static IScrapperController scrapperController;
    private static Gson gson;

    public static void main(String[] args) {



        initialize(args);
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
                    res.type("json");

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
                    res.type("json");
                    return gson.toJson(dto);

                }
        );

        get("/hello", (req, res)->"Hello, world");
    }


    private static void initialize(String[] args){

        Map<String, String> argsMap = new LinkedHashMap<>();
        for (String arg: args) {
            String[] parts = arg.split("=", 2);

            if(parts.length != 2){
                log.error("cant decode argument arg {}", arg);
                continue;
            }
            argsMap.put(parts[0], parts[1]);
        }

        IRequestService requestService = new RequestService();

        Database fakeDatabase = new FakeDatabase(new ConcurrentHashMap<>());

        IDoScrapper doScrapper = new DoScrapper();

        ICreateScrapperService createScrapperService = new CreateScrapperService(fakeDatabase, argsMap.get("BASE_URL"),
                doScrapper, requestService);

        IGetScrapperService getScrapperService = new GetScrapperService(fakeDatabase);

        scrapperController = new ScrapperController(createScrapperService, getScrapperService);
        gson = new Gson();

    }
}
