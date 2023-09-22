//package com.base.gatewayservice.config;
//
//import com.base.gatewayservice.filter.RequestValidationGatewayFilterFactory;
//import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.http.HttpMethod;
//
//@Configuration
//public class GatewayConfig {
//
////    @Bean
////    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
////        return builder.routes()
////                .route("microservico-route", r -> r
////                        .path("/clients")
////                        .and().method(HttpMethod.GET)
////                        .filters(f -> f
////                                .filter(new RequestValidationGatewayFilterFactory(new ModifyRequestBodyGatewayFilterFactory())
////                                        .apply(new RequestValidationGatewayFilterFactory
////                                                .Config(RequestValidationGatewayFilterFactory.ConfigType.FORM_DATA_TO_BODY))))
////                        .uri("")
////                ).build();
////    }
//
//
//}
