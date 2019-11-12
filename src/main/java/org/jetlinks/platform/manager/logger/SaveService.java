package org.jetlinks.platform.manager.logger;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public interface SaveService {

//    <T> boolean bulkSave(List<T> data, Object saveTarget);
//
//    <T> boolean save(T data, Object saveTarget);

    <T> Mono<Boolean> asyncBulkSave(List<T> data, Object saveTarget);

    <T> Mono<Boolean> asyncSave(T data, Object saveTarget);
}
