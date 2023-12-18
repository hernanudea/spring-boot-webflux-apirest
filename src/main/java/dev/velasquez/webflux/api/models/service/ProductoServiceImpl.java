package dev.velasquez.webflux.api.models.service;

import dev.velasquez.webflux.api.models.dao.CategoriaDao;
import dev.velasquez.webflux.api.models.dao.ProductoDao;
import dev.velasquez.webflux.api.models.documents.Categoria;
import dev.velasquez.webflux.api.models.documents.Producto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService{

	private final ProductoDao dao;
	
	private final CategoriaDao categoriaDao;

	public ProductoServiceImpl(ProductoDao dao, CategoriaDao categoriaDao) {
		this.dao = dao;
		this.categoriaDao = categoriaDao;
	}

	@Override
	public Flux<Producto> findAll() {
		return dao.findAll();
	}

	@Override
	public Mono<Producto> findById(String id) {
		return dao.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		return dao.save(producto);
	}

	@Override
	public Mono<Void> delete(Producto producto) {
		return dao.delete(producto);
	}

	@Override
	public Flux<Producto> findAllConNombreUpperCase() {
		return dao.findAll().map(producto -> {
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}

	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat() {
		return findAllConNombreUpperCase().repeat(5000);
	}

	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaDao.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		return categoriaDao.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaDao.save(categoria);
	}


}
