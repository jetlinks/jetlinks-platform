package org.jetlinks.platform.manager.notify.sms;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetlinks.platform.manager.notify.AbstractTemplateSmsSender;
import org.jetlinks.platform.manager.notify.SmsProvider;
import org.jetlinks.platform.manager.notify.SmsTemplateManager;
import org.jetlinks.rule.engine.executor.node.notify.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Hy2046SmsSenderProvider implements SmsProvider {


    private WebClient webClient = WebClient.builder()
            .baseUrl("http://sms10692.com/v2sms.aspx")
            .build();

    @Autowired
    private SmsTemplateManager templateManager;

    @Override
    public String getProvider() {
        return "hy2046";
    }

    @Override
    public String getName() {
        return "宏衍2046";
    }

    @Override
    public Mono<SmsSender> createSender(Map<String, Object> configuration) {

        return Mono.defer(() -> {
            String userId = (String) configuration.get("userId");
            String username = (String) configuration.get("username");
            String password = (String) configuration.get("password");
            Assert.hasText(userId, "短信配置错误,缺少userId");
            Assert.hasText(username, "短信配置错误,缺少username");
            Assert.hasText(password, "短信配置错误,缺少password");
            return Mono.just(new Hy2046SmsSender(userId, username, password));
        });
    }

    @AllArgsConstructor
    class Hy2046SmsSender extends AbstractTemplateSmsSender {

        String userId;
        String username;
        String password;

        @Override
        protected SmsTemplateManager getTemplateManager() {
            return templateManager;
        }

        @Override
        protected Mono<Boolean> doSend(String text, Map<String, Object> context, List<String> sendTo) {
            return Mono.defer(() -> {
                String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
                String sign = DigestUtils.md5Hex(username.concat(password).concat(ts));
                String mobile = String.join(",", sendTo);
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("userid", userId);
                formData.add("timestamp", ts);
                formData.add("sign", sign);
                formData.add("mobile", mobile);
                formData.add("content", text);
                formData.add("action", "send");
                formData.add("rt", "json");

                return webClient.post()
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(BodyInserters.fromFormData(formData))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(map -> {
                            if (Integer.valueOf(sendTo.size()).equals(map.get("SuccessCounts"))) {
                                return true;
                            }
                            throw new RuntimeException("发送短信失败:" + map.get("Message"));
                        });

            });
        }

    }
}

