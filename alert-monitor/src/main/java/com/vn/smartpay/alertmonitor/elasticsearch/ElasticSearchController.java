package com.vn.smartpay.alertmonitor.elasticsearch;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElasticSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);
    public static Gson gson = new Gson();
    public static RestHighLevelClient client;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public static RestHighLevelClient client() {
        if (client == null) {
            RestClientBuilder builder = RestClient.builder(new HttpHost("10.1.1.108", 9200, "http"));
            client = new RestHighLevelClient(builder);
        }

        return client;
    }

    public void executeElastic() throws IOException {
        SearchResponse response = searchAgg("account");
        System.out.println(gson.toJson(response));
//        executor.execute(new Runnable() {
//            public void run() {
//                getAllIndices();
//            }
//        });
//        executor.shutdown();
    }

    public void getAllIndices() {

        Map<String, Integer> result = new HashMap<>();
        Map<String, Integer> mapService = new HashMap<>();
        try {
            String[] indices = getAllIndex();
            System.out.println(gson.toJson(indices));
            for (String index : indices) {
                SearchResponse response = this.search(index);
                if (response == null || response.getHits() == null) {
                    continue;
                }
                SearchHits searchHits = response.getHits();
                if (searchHits.getHits() == null) {
                    continue;
                }
                SearchHit[] hits = searchHits.getHits();
                for (SearchHit item : hits) {
                    Map<String, Object> source = item.getSourceAsMap();
                    Integer size = Integer.parseInt(String.valueOf(source.getOrDefault("gl2_accounted_message_size", 0)));
                    String module = String.valueOf(source.getOrDefault("module", "module"));
                    System.out.println("module: " + module + " - " + "size: " + size);
                    mapService.put(module, size);
                    String key = index + "-" + module;
                    if (result.containsKey(key)) {
                        Integer count = result.getOrDefault(key, 0) + size;
                        result.put(key, count);
                    } else {
                        result.put(key, size);
                    }
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        System.out.println(gson.toJson(result));
        System.out.println("module:" + gson.toJson(mapService));
    }

    public SearchResponse search(String indices) throws IOException {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchAllQuery())
                    .postFilter(QueryBuilders.rangeQuery("endTime").from(1619110800000L).to(1619197199000L)) //gt 24
                    .from(0)
                    .size(50)
                    .sort(new FieldSortBuilder("endTime").order(SortOrder.ASC))
                    .sort(new ScoreSortBuilder().order(SortOrder.DESC));

            SearchRequest searchRequest = new SearchRequest()
                    .indices(indices)
                    .source(sourceBuilder);

            return ElasticSearchController.client().search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
//            ex.printStackTrace();
            return null;
        }
    }

    public SearchResponse searchAgg(String indices) throws IOException {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            TermsAggregationBuilder aggregation = AggregationBuilders.terms("source_node.host")
//                    .field("source_node.host");
//            aggregation.subAggregation(AggregationBuilders.sum("interval_ms").field("interval_ms")).order(BucketOrder.count(true));
//            searchSourceBuilder.aggregation(aggregation);
            sourceBuilder.query(QueryBuilders.matchAllQuery()).size(5);
            SearchRequest searchRequest = new SearchRequest()
                    .indices(indices)
                    .source(sourceBuilder);
            SearchResponse response = ElasticSearchController.client().search(searchRequest, RequestOptions.DEFAULT);
            return ElasticSearchController.client().search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String[] getAllIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("*");
        GetIndexResponse response = ElasticSearchController.client().indices().get(request, RequestOptions.DEFAULT);
        String[] indices = response.getIndices();
        return indices;
    }

}

