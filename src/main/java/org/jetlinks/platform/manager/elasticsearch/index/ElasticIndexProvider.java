package org.jetlinks.platform.manager.elasticsearch.index;

/**
 * @version 1.0
 **/
public interface ElasticIndexProvider {

    String getIndex();

    String getType();

    static ElasticIndexProvider createIndex(String index, String type) {
        return new ElasticIndexProvider() {
            @Override
            public String getIndex() {
                return index;
            }

            @Override
            public String getType() {
                return type;
            }
        };
    }
}
