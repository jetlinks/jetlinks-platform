package org.jetlinks.platform.manager.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.hswebframework.easyorm.elasticsearch.enums.LinkTypeEnum;
import org.hswebframework.ezorm.core.param.QueryParam;
import org.jetlinks.platform.manager.enums.EsDataType;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
public class QueryParamTranslator {


    public static SearchRequest translate(QueryParam queryParam, EsDataType dataType) {
        SearchRequest request = new SearchRequest(dataType.getIndex())
                .types(dataType.getType())
                .source(transSourceBuilder(queryParam));
        log.debug("es查询参数:{}", request.source().toString());
        return request;
    }
    public static SearchRequest translate(QueryParam queryParam, String index, String type) {
        SearchRequest request = new SearchRequest(index)
                .types(type)
                .source(transSourceBuilder(queryParam));
        log.debug("es查询参数:{}", request.source().toString());
        return request;
    }

    public static QueryBuilder translate(QueryParam queryParam) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        Objects.requireNonNull(queryParam, "QueryParam must not null.")
                .getTerms()
                .forEach(term -> LinkTypeEnum.of(term.getType().name())
                        .ifPresent(e -> e.process(query, term)));
        return query;
    }

    private static SearchSourceBuilder transSourceBuilder(QueryParam queryParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (queryParam.isPaging()) {
            sourceBuilder.from(queryParam.getPageIndex() * queryParam.getPageSize());
            sourceBuilder.size(queryParam.getPageSize());
        }
        queryParam.getSorts()
                .forEach(sort -> {
                    if (!StringUtils.isEmpty(sort.getName())) {
                        sourceBuilder.sort(sort.getName(), SortOrder.fromString(sort.getOrder()));
                    }

                });
        return sourceBuilder.query(translate(queryParam));
    }
}
