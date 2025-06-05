package com.maximorero.k8_auth.rest_client;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class TracingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    
    private final TextMapSetter<HttpHeaders> setter = HttpHeaders::set;
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        
        // Obtener el contexto actual
        Context context = Context.current();
        
        // Propagar el contexto a las cabeceras HTTP
        GlobalOpenTelemetry.getPropagators().getTextMapPropagator()
            .inject(context, request.getHeaders(), setter);
        
        return execution.execute(request, body);
    }
}