//package org.jetlinks.platform.manager.excel;
//
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.hswebframework.expands.office.excel.ExcelIO;
//import org.hswebframework.expands.office.excel.config.ExcelWriterConfig;
//import org.hswebframework.expands.office.excel.config.Header;
//import org.hswebframework.expands.office.excel.wrapper.HashMapWrapper;
//import org.hswebframework.expands.request.RequestBuilder;
//import org.hswebframework.web.bean.FastBeanCopier;
//import org.hswebframework.web.exception.BusinessException;
//import org.hswebframework.web.id.IDGenerator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
///**
// * @author bsetfeng
// * @since 1.0
// **/
//@Slf4j
//@Component
//public class DefaultImportExportService implements ImportExportService {
//
//    @Autowired(required = false)
//    private RequestBuilder requestBuilder;
//
//
//    @Override
//    public <T> void doImport(String fileUrl, Class<T> type, Map<String, String> headerNameMapper, Function<T, Boolean> consumerData) {
//        try {
//            InputStream in = getExcelInputStream(fileUrl);
//            readExcel(in, type, headerNameMapper, consumerData);
//        } catch (Exception e) {
//            log.error("excel文件读取失败:{}", e);
//            throw new BusinessException("excel文件读取失败", e);
//        }
//    }
//
//    @Override
//    public void writeImportExcelTemplate(OutputStream outputStream, Map<String, String> header) {
//        ExcelWriterConfig config = new ExcelWriterConfig();
//        config.setHeaders(createExcelImportHeaders(header));
//        writeData(outputStream, config);
//    }
//
//    private <T> void readExcel(InputStream in, Class<T> type, Map<String, String> headerNameMapper, Function<T, Boolean> consumerFunction) throws Exception {
//        //重新包装表头映射
//        HashMapWrapper wrapper = new HashMapWrapper() {
//            @Override
//            @SneakyThrows
//            public boolean wrapperDone(Map<String, Object> instance) {
//                T value = type.newInstance();
//                if (!consumerFunction.apply(FastBeanCopier.copy(instance, value))) {
//                    shutdown();
//                }
//                return false;
//            }
//        };
//        wrapper.setHeaderNameMapper(headerNameMapper);
//        ExcelIO.read(in, wrapper);
//    }
//
//    private InputStream getExcelInputStream(String fileUrl) throws Exception {
//        //创建一个临时文件
//        File file = File.createTempFile(IDGenerator.MD5.generate(), ".xlsx");
//        //下载上传的文件
//        requestBuilder
//                .http(fileUrl)
//                .download()
//                .write(file);
//        InputStream in = new FileInputStream(file);
//        return in;
//    }
//
//    private List<Header> createExcelImportHeaders(Map<String, String> header) {
//        return header.entrySet().stream()
//                .map(entry -> new Header(entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 写出excel数据
//     *
//     * @param outputStream 输出流
//     * @param config       excel写入配置
//     */
//    private void writeData(OutputStream outputStream, ExcelWriterConfig config) {
//        try {
//            ExcelIO.write(outputStream, config);
//        } catch (Exception e) {
//            log.error("写出excel数据失败:{}", e);
//            throw new BusinessException("写出excel数据失败", e);
//        }
//    }
//}
