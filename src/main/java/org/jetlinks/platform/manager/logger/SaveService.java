package org.jetlinks.platform.manager.logger;

import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public interface SaveService {

    <T> boolean bulkSave(List<T> data, Object saveTarget);

    <T> void asyncBulkSave(List<T> data, Object saveTarget);
}
