package com.utm.search.engine.controller;

import com.utm.search.engine.model.Query;
import com.utm.search.engine.model.Redirect;
import com.utm.search.engine.model.Root;
import com.utm.search.engine.model.SearchEngineConstants;
import com.utm.search.engine.model.SearchResponse;
import com.utm.search.engine.service.ScrapeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final ScrapeService scrapeService;

    @GetMapping("autocomplete")
    public List<String> autocomplete(@RequestParam String searchWord, @RequestParam(defaultValue = "20") String limit) {
        var restTemplate = new RestTemplate();
        Query query = restTemplate.getForEntity(String.format(SearchEngineConstants.AUTOCOMPLETE_URL, searchWord, limit), Root.class).getBody().getQuery();

        return query.getRedirects().stream().map(Redirect::getFrom).sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
    }

    @PostMapping("search")
    public List<SearchResponse> search(@RequestParam List<String> words) {
        return scrapeService.search(words);
    }
}
