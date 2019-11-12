package org.jetlinks.platform.manager.excel.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 不能用spring管理，每次调用都需要new
 *
 * @author bsetfeng
 * @since 1.0
 **/
@Slf4j
public class DeviceInstanceDataListener<T> extends AnalysisEventListener<T> {


    private FluxSink<T> sink;

    public DeviceInstanceDataListener(FluxSink<T> sink) {
        this.sink = sink;
    }


   public static <T> Flux<T> of(String url){
     return Flux.create(sink->{
          EasyExcel.read(url, new DeviceInstanceDataListener<>(sink)).sheet().doRead();
      });
   }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
         sink.error(exception);
    }

    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        sink.next(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        sink.complete();
    }

    @Override
    public boolean hasNext(AnalysisContext context) {

        return ! sink.isCancelled();
    }
}
