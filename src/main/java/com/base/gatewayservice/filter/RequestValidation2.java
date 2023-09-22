package com.base.gatewayservice.filter;

import com.base.gatewayservice.filter.function.MyRewriteFunction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestValidation2 extends AbstractGatewayFilterFactory<RequestValidation2.Config> {

    private final ModifyRequestBodyGatewayFilterFactory factory;

    public RequestValidation2(ModifyRequestBodyGatewayFilterFactory factory) {
        super(Config.class);
        this.factory = factory;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> new RequestValidationFilter(factory).filter(exchange, chain);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Config{
        private boolean formdataToBody;
        private boolean formdataNotEmpty;
    }

}
