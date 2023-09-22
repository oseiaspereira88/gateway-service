package com.base.gatewayservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;

@Component
@Slf4j
public class RequestValidationWithDecorator extends AbstractGatewayFilterFactory<RequestValidationWithDecorator.Config> {

    public RequestValidationWithDecorator() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            Map<String, String> valueMap = new HashMap<>();
            valueMap.put("name", "HBO");
            valueMap.put("imageSrc", "/image");
            valueMap.put("businessName", "HBO Ltda.");

            RequestDecorator decorator = new RequestDecorator(exchange.getRequest(), valueMap);
            return chain.filter(exchange.mutate().request(decorator).build()).then();
        };
    }

    public static class RequestDecorator extends ServerHttpRequestDecorator {

        private final Map<String, String> values;

        public RequestDecorator(ServerHttpRequest delegate, Map<String, String> values) {
            super(delegate);
            this.values = values;
        }

        @Override
        public Flux<DataBuffer> getBody(){
            try{
                String newBody = new ObjectMapper().writeValueAsString(values);
                DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
                DefaultDataBuffer buffer = factory.wrap(newBody.getBytes());

                return Flux.just(buffer);
            } catch (JsonProcessingException e) {
                return super.getBody();
            }
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Config {
        private boolean formdataToBody;
        private boolean formdataNotEmpty;
    }

}
