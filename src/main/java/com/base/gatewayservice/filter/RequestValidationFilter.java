package com.base.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestValidationFilter implements GatewayFilter {

    private final ModifyRequestBodyGatewayFilterFactory factory;

    public RequestValidationFilter(ModifyRequestBodyGatewayFilterFactory factory) {
        this.factory = factory;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("name", "HBO");
        valuesMap.put("imageSrc", "/image");
        valuesMap.put("businessName", "HBO Ltda.");

        ModifyRequestBodyGatewayFilterFactory.Config cfg = new ModifyRequestBodyGatewayFilterFactory.Config();
        cfg.setRewriteFunction(String.class, String.class, new RequestBodyRewriteFunction(valuesMap));

        GatewayFilter modifyBodyFilter = factory.apply(cfg);

        return modifyBodyFilter.filter(exchange, ch -> Mono.empty())
                .then(chain.filter(exchange));
    }

}
