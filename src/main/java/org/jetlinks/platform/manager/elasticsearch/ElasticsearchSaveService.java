package org.jetlinks.platform.manager.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.exception.BusinessException;
import org.jetlinks.platform.manager.entity.ElasticSearchIndexEntity;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.jetlinks.platform.manager.logger.SaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
@Component
public class ElasticsearchSaveService implements SaveService {

    @Autowired
    private JestClient jestClient;

    @Override
    public <T> boolean bulkSave(List<T> data, Object saveTarget) {
        return save(data, judgeTargetType(saveTarget));
    }

    @Override
    public <T> void asyncBulkSave(List<T> data, Object saveTarget) {
        asyncSave(data, judgeTargetType(saveTarget));
    }

    private ElasticSearchIndexEntity judgeTargetType(Object saveTarget) {
        if (saveTarget instanceof ElasticSearchIndexEntity) {
            return (ElasticSearchIndexEntity) saveTarget;
        } else if (saveTarget instanceof EsDataType) {
            EsDataType type = (EsDataType) saveTarget;
            return new ElasticSearchIndexEntity(type.getIndex(), type.getType());
        } else {
            throw new BusinessException("es不支持的批量存储目标类型:" + saveTarget.getClass());
        }
    }


    private <T> boolean save(List<T> data, ElasticSearchIndexEntity entity) {
        Bulk.Builder bulk = new Bulk.Builder()
                .defaultIndex(entity.getIndex())
                .defaultType(entity.getType());
        data.forEach(d -> bulk.addAction(new Index.Builder(d).build()));
        try {
            BulkResult result = jestClient.execute(bulk.build());
            if (result != null && result.isSucceeded()) {
                log.debug("批量存储数据到es成功，size:{}", data.size());
            }
            return result != null && result.isSucceeded();
        } catch (IOException e) {
            log.error("es 批量存储失败，原因:{}", e.getMessage());
        }
        return false;
    }

    private <T> void asyncSave(List<T> data, ElasticSearchIndexEntity entity) {
        Bulk.Builder builder = new Bulk.Builder()
                .defaultIndex(entity.getIndex())
                .defaultType(entity.getType());
        data.forEach(d -> builder.addAction(new Index.Builder(d).build()));
        jestClient.executeAsync(builder.build(), new JestResultHandler<JestResult>() {
            @Override
            public void completed(JestResult result) {
                if (!result.isSucceeded()) {
                    log.error("批量存储数据到es失败:{}", result.getJsonString());
                } else {
                    log.info("批量存储数据到es成功:size{}", data.size());
                }
            }

            @Override
            public void failed(Exception ex) {
                log.error("批量存储数据到es失败", ex);
            }
        });
    }
}
