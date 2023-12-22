package dev.velasquez.webflux.api.models.dao;

import dev.velasquez.webflux.api.models.documents.Producto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {

    public Mono<Producto> findByNombre(String nombre);

    //usando Quewry seria asi:
    @Query("{ 'nombre' : ?0 }")
    public Mono<Producto> buscarPorNombre(String nombre);

}
