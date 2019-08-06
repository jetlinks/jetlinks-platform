package org.jetlinks.platform.manager.web;

import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.id.IDGenerator;
import org.jetlinks.core.device.registry.DeviceRegistry;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/device")
public class DeviceMessageController {

    @Autowired
    private DeviceRegistry registry;

    @Autowired
    private LocalDeviceInstanceService localDeviceInstanceService;

    //强制同步设备真实状态到数据库
    @PutMapping("/state/sync")
    public ResponseMessage<Void> syncState(@RequestBody List<String> deviceIdList) {

        localDeviceInstanceService.syncState(deviceIdList, true);

        return ResponseMessage.ok();
    }

    //获取设备属性
    @GetMapping("/{deviceId}/property/{property:.+}")
    @SneakyThrows
    public ResponseMessage<?> getProperties(@PathVariable String deviceId, @PathVariable String property) {
        return registry.getDevice(deviceId)
                .messageSender()
                .readProperty(property.split("[, ;]"))
                .messageId(IDGenerator.SNOW_FLAKE_STRING.generate())
                .trySend(10, TimeUnit.SECONDS)
                .map(reply -> {
                    if (reply.isSuccess()) {
                        return ResponseMessage.ok(reply.getProperties());
                    } else {
                        return ResponseMessage.error(500, reply.getMessage()).code(reply.getCode());
                    }
                })
                .recover(error -> ResponseMessage.error(error.getMessage()))
                .get();
    }

}
