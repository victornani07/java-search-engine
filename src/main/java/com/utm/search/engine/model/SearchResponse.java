package com.utm.search.engine.model;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResponse {
    private String url;
    private String title;
    private String description;
}
