package org.jetlinks.platform.events.handler;

import lombok.extern.slf4j.Slf4j;
import org.jetlinks.core.metadata.DataType;
import org.jetlinks.core.metadata.PropertyMetadata;
import org.jetlinks.core.metadata.types.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
public class PropertyValueTrans {

    public static Object transValueFormat(Object value, List<PropertyMetadata> metadataList) {
        if (value instanceof Map) {
            return transMap((Map<String, Object>) value, metadataList);
        } else {
            return value;
        }
    }

    private static Map<String, Object> transMap(Map<String, Object> map, List<PropertyMetadata> metadataList) {
        Map<String, PropertyMetadata> metadataMap = transMetadataToMapStructure(metadataList);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            PropertyMetadata property = metadataMap.get(entry.getKey());
            if (null != property) {
                entry.setValue(transValue(entry.getValue(), property.getValueType()));
            }
        }
        return map;
    }

    private static Map<String, PropertyMetadata> transMetadataToMapStructure(List<PropertyMetadata> metadata) {
        return metadata.stream()
                .collect(Collectors.toMap(PropertyMetadata::getId, Function.identity()));
    }

    private static Object transValue(Object value, DataType dataType) {
        try {
            if (dataType instanceof DateTimeType) {
                return transDateFormat(((DateTimeType) dataType).convert(value));
            } else if (dataType instanceof DoubleType) {
                return ((DoubleType) dataType).convert(value);
            } else if (dataType instanceof FloatType) {
                return ((FloatType) dataType).convert(value);
            } else if (dataType instanceof LongType) {
                return ((LongType) dataType).convert(value);
            } else if (dataType instanceof BooleanType) {
                return ((BooleanType) dataType).convert(value);
            } else if (dataType instanceof IntType) {
                return ((IntType) dataType).convert(value);
            } else if (dataType instanceof ObjectType) {
                return transValueFormat(value, ((ObjectType) dataType).getProperties());
            } else {
                return value;
            }
        } catch (Exception e) {
            log.error("设备上报值与元数据值不匹配.value:{},DataTypeClass:{}", value, dataType.getClass());
            return value;
        }
    }

    public static String transDateFormat(Date date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.format(dtf);
    }
}
