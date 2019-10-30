package org.jetlinks.platform.manager.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.authorization.annotation.QueryAction;
import org.hswebframework.web.authorization.annotation.Resource;
import org.hswebframework.web.crud.web.reactive.ReactiveServiceCrudController;
import org.jetlinks.core.ProtocolSupport;
import org.jetlinks.core.ProtocolSupports;
import org.jetlinks.platform.manager.entity.DeviceInstanceEntity;
import org.jetlinks.platform.manager.entity.DevicePropertiesEntity;
import org.jetlinks.platform.manager.entity.ProtocolSupportEntity;
import org.jetlinks.platform.manager.service.LocalDeviceInstanceService;
import org.jetlinks.platform.manager.service.LocalDevicePropertiesService;
import org.jetlinks.platform.manager.service.LocalProtocolSupportService;
import org.jetlinks.platform.manager.web.response.DeviceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protocol")
@Authorize
@Resource(id = "protocol-supports", name = "协议管理")
public class ProtocolSupportController implements
        ReactiveServiceCrudController<ProtocolSupportEntity, String> {

    @Autowired
    @Getter
    private LocalProtocolSupportService service;

    @Autowired
    private ProtocolSupports protocolSupports;

    @PostMapping("/{id}/_deploy")
    public Mono<Boolean> deploy(@PathVariable String id) {
        return service.deploy(id);
    }

    @PostMapping("/{id}/_un-deploy")
    public Mono<Boolean> unDeploy(@PathVariable String id) {
        return service.unDeploy(id);
    }

    @GetMapping("/supports")
    public Flux<ProtocolInfo> allProtocols() {
        return protocolSupports.getProtocols()
                .map(ProtocolInfo::of);
    }


    @Getter
    @Setter
    @AllArgsConstructor(staticName = "of")
    @NoArgsConstructor
    public static class ProtocolInfo {
        private String id;

        private String name;

        static ProtocolInfo of(ProtocolSupport support) {
            return of(support.getId(), support.getName());
        }
    }
}
