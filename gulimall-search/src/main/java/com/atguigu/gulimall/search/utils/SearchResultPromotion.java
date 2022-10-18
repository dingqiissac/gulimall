package com.atguigu.gulimall.search.utils;

import com.google.gson.JsonElement;
import io.searchbox.core.SearchResult;

public class SearchResultPromotion extends SearchResult {

    public SearchResultPromotion(SearchResult searchResult) {
        super(searchResult);
    }

    public Long getTotal() {
        Long total = null;
        JsonElement obj = getPath(PATH_TO_TOTAL);
        if (obj != null) total = obj.getAsJsonObject().get("value").getAsLong();
        return total;
    }

}
