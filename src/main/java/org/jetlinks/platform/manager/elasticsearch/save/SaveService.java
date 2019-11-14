package org.jetlinks.platform.manager.elasticsearch.save;

import org.jetlinks.platform.manager.elasticsearch.index.ElasticIndexProvider;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public interface SaveService {

    <T> Mono<Boolean> asyncBulkSave(List<T> data, ElasticIndexProvider provider);

    <T> Mono<Boolean> asyncSave(T data, ElasticIndexProvider provider);
}
