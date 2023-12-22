package dev.velasquez.webflux.api.models.service;


import dev.velasquez.webflux.api.models.documents.Categoria;
import dev.velasquez.webflux.api.models.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<Producto> findAll();

    Flux<Producto> findAllConNombreUpperCase();

    Flux<Producto> findAllConNombreUpperCaseRepeat();

    Mono<Producto> findById(String id);

    Mono<Producto> save(Producto producto);

    Mono<Void> delete(Producto producto);

    Flux<Categoria> findAllCategoria();

    Mono<Categoria> findCategoriaById(String id);

    Mono<Categoria> saveCategoria(Categoria categoria);

    Mono<Producto> findByNombre(String nombre);

    Mono<Producto> buscarPorNombre(String nombre);

    Mono<Categoria> findCategoriaByNombre(String nombre);
}
