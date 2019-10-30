package org.jetlinks.platform.manager.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ElasticRestClient {

    private String host;

    private int port;

    public RestHighLevelClient getClient(){
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, "http")));
    }
}
