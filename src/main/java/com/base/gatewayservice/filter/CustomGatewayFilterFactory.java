package com.base.gatewayservice.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

    public CustomGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Lógica para validar dados do FormData, se presente na requisição
            if (config.isValidateFormData()) {
                ServerWebExchange modifiedExchange = validateFormData(exchange, chain);
                if (modifiedExchange != null) {
                    exchange = modifiedExchange;
                } else {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.BAD_REQUEST);

                    return response.setComplete();
                }
            }

            ServerWebExchange finalExchange = exchange;
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // Lógica para manipular o corpo da resposta, se necessário
                        if (config.isManipulateResponse()) {
                            manipulateResponse(finalExchange);
                        }
                    }))
                    .onErrorResume(ex -> {
                        // Tratamento de exceções
                        return handleException(ex, finalExchange);
                    }).then();
        };
    }

    private ServerWebExchange validateFormData(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isValidFormData(exchange)) {
            // Se a validação for bem-sucedida, continue com a cadeia de filtros.
            return exchange;
        } else {
            // Se a validação falhar, retorne uma resposta de Bad Request.
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().setComplete();
        }
        return exchange;
    }

    private boolean isValidFormData(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest mutateRequest = request.mutate().build();

        String sessionParam = mutateRequest.getQueryParams().getFirst("sessionParam");

        if (sessionParam != null && !sessionParam.isEmpty()) {
//            exchange.getRequest().getBody().body("{\"sessionParam\":\"" + sessionParam + "\"}");
//
//            // Remova o parâmetro do FormData.
//            mutateRequest.queryParams(params -> params.remove("sessionParam"));

            return true; // A validação foi bem-sucedida.
        }

        return false;
    }

    private void manipulateResponse(ServerWebExchange exchange) {
        // Implemente a manipulação do corpo da resposta aqui, se necessário.
        // Você pode acessar o ServerHttpResponse para modificar o corpo da resposta.
    }

    private Mono<Void> handleException(Throwable ex, ServerWebExchange exchange) {
        // TODO(Tratamento da exceção e resposta de erro personalizada)
        return Mono.empty();
    }

    public static class Config{

        public Config(ConfigType type){
            if(type == ConfigType.VALIDATE_FORM_DATA){
                validateFormData = true;
            } else if(type == ConfigType.MANIPULATE_RESPONSE){
                manipulateResponse = true;
            }
        }

        private boolean validateFormData;
        private boolean manipulateResponse;

        public boolean isValidateFormData() {
            return validateFormData;
        }

        public void setValidateFormData(boolean validateFormData) {
            this.validateFormData = validateFormData;
        }

        public boolean isManipulateResponse() {
            return manipulateResponse;
        }

        public void setManipulateResponse(boolean manipulateResponse) {
            this.manipulateResponse = manipulateResponse;
        }
    }

    public enum ConfigType{
        VALIDATE_FORM_DATA,
        MANIPULATE_RESPONSE
    }

}
