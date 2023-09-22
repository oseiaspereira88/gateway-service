package com.base.gatewayservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestValidationWithDecorator2 extends AbstractGatewayFilterFactory<RequestValidationWithDecorator2.Config> {

    public RequestValidationWithDecorator2() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->
                chain.filter(new RequestExchangeDecorator(exchange, Config.getValueMap()));
    }

    public static class RequestDecorator extends ServerHttpRequestDecorator {

        private String body = null;

        private final Map<String, String> values;

        public RequestDecorator(ServerHttpRequest delegate, Map<String, String> values) {
            super(delegate);
            this.values = values;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            try {
                DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
                String newBody = new ObjectMapper().writeValueAsString(values);
                byte[] bytes = newBody.getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
                return Flux.just(buffer.write(bytes));

            } catch (JsonProcessingException e) {
                return super.getBody();
            }
        }

        public String getRequestBody() {
            return this.body;
        }

    }

    public class RequestExchangeDecorator extends ServerWebExchangeDecorator {

        private final RequestDecorator request;

        public RequestExchangeDecorator(ServerWebExchange exchange, Map<String, String> values) {
            super(exchange);
            this.request = new RequestDecorator(exchange.getRequest(), values);
        }

        @Override
        public RequestDecorator getRequest() {
            return request;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Config {
        private boolean formdataToBody;
        private boolean formdataNotEmpty;

        public static Map<String, String> getValueMap() {
            return new HashMap<>() {{
                put("name", "HBO");
                put("imageSrc", "/image");
                put("businessName", "HBO Ltda.");
            }};
        }
    }

}
