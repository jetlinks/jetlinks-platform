package org.jetlinks.platform.configuration;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.platform.manager.elasticsearch.ElasticRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Configuration
@Slf4j
public class ElasticSearchConfiguration {

    @Value("${elasticsearch.client.host:localhost}")
    private String host = "localhost";

    @Value("${elasticsearch.client.port:9200}")
    private int port = 9200;

    @Value("${elasticsearch.client.username:admin}")
    private String username = "admin";

    @Value("${elasticsearch.client.username:m123}")
    private String password = "m123";

    @Bean
    public ElasticRestClient restClient() {
        return new ElasticRestClient(host, port);
    }

//    protected void buildClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials(username, password));
//        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(host, port))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
//        client = clientBuilder.build();
//        restHighLevelClient = new RestHighLevelClient(clientBuilder);
//    }
}
