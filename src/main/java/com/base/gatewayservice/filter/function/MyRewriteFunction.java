package com.base.gatewayservice.filter.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MyRewriteFunction implements RewriteFunction<String, String> {

    private final Map<String, String> values;

    public MyRewriteFunction(Map<String, String> values) {
        this.values = values;
    }

    @Override
    public Publisher<String> apply(ServerWebExchange serverWebExchange, String oldBody) {
        try{
            String newBody = new ObjectMapper().writeValueAsString(values);
            return Mono.just(newBody);
        } catch (Exception e){
            return Mono.just(oldBody);
        }
    }

    @Override
    public <V> BiFunction<ServerWebExchange, String, V> andThen(Function<? super Publisher<String>, ? extends V> after) {
        return RewriteFunction.super.andThen(after);
    }
}
