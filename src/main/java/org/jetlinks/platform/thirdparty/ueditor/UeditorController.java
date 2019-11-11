package org.jetlinks.platform.thirdparty.ueditor;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.file.FileUploadProperties;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ueditor")
public class UeditorController {

    protected Map<String, Object> config;

    @Autowired
    protected FileUploadProperties properties;

    @PostConstruct
    @SneakyThrows
    public void init() {
        config = JSON.parseObject(StreamUtils.copyToString(new ClassPathResource("ueditor-config.json").getInputStream(), StandardCharsets.UTF_8));
    }

    @GetMapping(params = "action=config")
    @Authorize(ignore = true)
    public Mono<ResponseEntity<Object>> getConfig() {
        return Mono.just(ResponseEntity.ok(config));
    }


    @PostMapping(params = "action=uploadimage")
    public Mono<ResponseEntity<Object>> uploadImage(@RequestPart("upfile") FilePart upfile) {
        return uploadFile(upfile);
    }

    @PostMapping(params = "action=uploadvideo")
    public Mono<ResponseEntity<Object>> uploadVideo(@RequestPart("upfile") FilePart upfile) {
        return uploadFile(upfile);
    }

    @PostMapping(params = "action=uploadfile")
    public Mono<ResponseEntity<Object>> uploadFile(@RequestPart("upfile") FilePart upfile) {
        FileUploadProperties.StaticFileInfo fileInfo = properties.createStaticSavePath(upfile.filename());
        File file = new File(fileInfo.getSavePath());

        return (upfile)
                .transferTo(file)
                .thenReturn(fileInfo.getLocation())
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", upfile.filename());
                    map.put("state", "SUCCESS");
                    map.put("original", upfile.filename());
                    map.put("size", file.length());

                    map.put("url", fileInfo.getLocation());
                    return ResponseEntity.ok(map);
                });
    }

    @PostMapping(params = "action=uploadscrawl", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<ResponseEntity<Object>> uploadFile(ServerWebExchange exchange) {
        String fileName = IDGenerator.SNOW_FLAKE_STRING.generate() + ".png";
        FileUploadProperties.StaticFileInfo fileInfo = properties.createStaticSavePath(fileName);

        return exchange.getFormData()
                        .flatMap(mv -> Mono.justOrEmpty(mv.getFirst("upfile")))
                        .flatMap(upfile ->
                                Mono.fromCallable(() -> {
                                    Files.write(Paths.get(fileInfo.getSavePath()),
                                            Base64.decodeBase64(upfile),
                                            StandardOpenOption.CREATE_NEW,
                                            StandardOpenOption.WRITE);

                                    return fileInfo.getLocation();
                                }))
                        .map(r -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", fileName);
                            map.put("state", "SUCCESS");
                            map.put("original", fileName);
                            map.put("url", fileInfo.getLocation());
                            return ResponseEntity.ok(map);
                        });
    }


}
