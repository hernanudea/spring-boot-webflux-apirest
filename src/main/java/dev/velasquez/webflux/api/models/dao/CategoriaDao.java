package dev.velasquez.webflux.api.models.dao;

import dev.velasquez.webflux.api.models.documents.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;



public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String>{

}
