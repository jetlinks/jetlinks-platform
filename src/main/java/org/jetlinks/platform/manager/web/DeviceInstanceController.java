package org.jetlinks.platform.manager.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.hswebframework.web.exception.NotFoundException;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.web.response.DeviceInfo;
import org.jetlinks.supports.official.JetLinksPropertyMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/device-instance")
//@Authorize
//@Resource(id = "device-instance", name = "设备实例")
public class DeviceInstanceController implements
        ReactiveServiceCrudController<DeviceInstanceEntity, String> {

    @Autowired
    @Getter
    private LocalDeviceInstanceService service;

    @GetMapping("/info/{id:.+}")
    @QueryAction
    public Mono<DeviceInfo> getDeviceInfoById(@PathVariable String id) {
        return service.getDeviceInfoById(id);
    }

    public static void main(String[] args) {
        String ss= "{\n" +
                "                  \"id\":\"name\",\n" +
                "                  \"name\":\"名称\",\n" +
                "                  \"valueType\":{\n" +
                "                      \"type\":\"string\"\n" +
                "                  }\n" +
                "              }";
        JSONObject object = JSONObject.parseObject(ss);
        JetLinksPropertyMetadata metadata = new JetLinksPropertyMetadata(object);
        System.out.println(metadata.getValueType().getId());
        System.out.println(metadata.getValueType().getName());
        System.out.println(JSON.toJSONString(metadata.toJson()));
    }

}
