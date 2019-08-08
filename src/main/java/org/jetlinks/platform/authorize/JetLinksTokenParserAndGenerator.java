package org.jetlinks.platform.authorize;

import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.basic.web.GeneratedToken;
import org.hswebframework.web.authorization.basic.web.ParsedToken;
import org.hswebframework.web.authorization.basic.web.UserTokenGenerator;
import org.hswebframework.web.authorization.basic.web.UserTokenParser;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Component
public class JetLinksTokenParserAndGenerator implements UserTokenParser, UserTokenGenerator {
    @Override
    public String getSupportTokenType() {
        return "JetLinks";
    }

    @Override
    public GeneratedToken generate(Authentication authentication) {
        String token = IDGenerator.MD5.generate();

        return new GeneratedToken() {
            @Override
            public Map<String, Object> getResponse() {
                return Collections.singletonMap("token",token);
            }

            @Override
            public String getToken() {
                return token;
            }

            @Override
            public String getType() {
                return getSupportTokenType();
            }

            @Override
            public int getTimeout() {
                return 7200*1000;
            }
        };
    }

    @Override
    public ParsedToken parseToken(HttpServletRequest request) {
        String header = request.getHeader("jlt");
        if(!StringUtils.hasText(header)){
            return null;
        }
        return new ParsedToken() {
            @Override
            public String getToken() {
                return header;
            }

            @Override
            public String getType() {
                return getSupportTokenType();
            }
        };
    }
}
