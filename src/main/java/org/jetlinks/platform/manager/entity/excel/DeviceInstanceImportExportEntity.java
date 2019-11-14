package org.jetlinks.platform.manager.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DeviceInstanceImportExportEntity {

    @ExcelProperty("设备id")
    private String id;

    @ExcelProperty("设备名称")
    private String name;

    @ExcelProperty("产品名称")
    private String productName;
}
