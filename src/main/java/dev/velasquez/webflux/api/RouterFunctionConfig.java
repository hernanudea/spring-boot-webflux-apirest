package dev.velasquez.webflux.api;

import dev.velasquez.webflux.api.handler.ProductoHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Value("${config.base.endpoint}")
    private String baseUrl;

//    private ProductoService productoService;
//
//    public RouterFunctionConfig(ProductoService productoService) {
//        this.productoService = productoService;
//    }

    //    @Bean // esta es una forma, pero la otra es preferible
//    public RouterFunction<ServerResponse> routes() {
//        return route(GET("/api/v2/productos").or(GET("/api/v3/productos")), request -> ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(productoService.findAll(), Producto.class));
//    }
    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler) {
        return route(GET("/api/v2/productos").or(GET("/api/v3/productos")), handler::listar)
                .andRoute(GET("/api/v2/productos/{id}"), handler::detalle)
                .andRoute(POST("/api/v2/productos").and(contentType(MediaType.APPLICATION_JSON)), handler::save)
                .andRoute(PUT("/api/v2/productos/{id}"), handler::update)
                .andRoute(DELETE("/api/v2/productos/{id}"), handler::delete)
                .andRoute(POST("/api/v2/productos/upload/{id}").and(contentType(MediaType.APPLICATION_JSON)), handler::upload)
                .andRoute(POST("/api/v2/productos/upload/{id}").and(contentType(MediaType.APPLICATION_JSON)), handler::upload)
                .andRoute(POST("/api/v2/productos/crear").and(contentType(MediaType.APPLICATION_JSON)), handler::saveWithPhoto);
    }
    // .andRoute(GET("/api/v2/productos/{id}").and(contentType(MediaType.APPLICATION_JSON)), handler::detalle);
    // al agregar el contentType aqui, se valida que el parametro de entrada sea aplication(json
    // se debe configurar el Content-Type en la cabecera de la peticion
}
