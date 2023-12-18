package dev.velasquez.webflux.api.models.dao;

import dev.velasquez.webflux.api.models.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{

}
