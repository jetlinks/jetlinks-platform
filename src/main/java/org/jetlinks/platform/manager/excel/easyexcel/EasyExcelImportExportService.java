package org.jetlinks.platform.manager.excel.easyexcel;


import com.alibaba.excel.EasyExcel;
import org.jetlinks.platform.manager.excel.ImportExportService;

import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;

/**
 * @author bsetfeng
 * @since 1.0
 **/
public class EasyExcelImportExportService implements ImportExportService {

    @Override
    public <T> void doImport(String fileUrl, Class<T> type, Map<String, String> headerNameMapper, Function<T, Boolean> consumerData) {
        EasyExcel.read().sheet();
    }

    @Override
    public void writeImportExcelTemplate(OutputStream outputStream, Map<String, String> header) {

    }
}
