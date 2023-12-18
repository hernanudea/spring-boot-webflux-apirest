package dev.velasquez.webflux.api.controller;

import dev.velasquez.webflux.api.models.documents.Producto;
import dev.velasquez.webflux.api.models.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

    @PostMapping("/v2")
    public Mono<ResponseEntity<Producto>> saveWithPhoto(Producto producto, @RequestPart FilePart file) {
        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }
        producto.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));

        return file.transferTo(new File(path + producto.getFoto())).then(productoService.save(producto))
                .map(p -> ResponseEntity.created(URI.create("api/productos/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file) {
        return productoService.findById(id).flatMap(p -> {
                    p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                            .replace(" ", "")
                            .replace(":", "")
                            .replace("\\", ""));
                    return file.transferTo(new File(path + p.getFoto())).then(productoService.save(p));
                }).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public ProductoRestController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping()
    public Mono<ResponseEntity<Flux<Producto>>> findAll() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(productoService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> findById(@PathVariable String id) {
        return productoService.findById(id)
                .map(producto -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(producto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public Mono<ResponseEntity<Map<String, Object>>> save(@Valid @RequestBody Mono<Producto> monoProducto) {
        Map<String, Object> response = new HashMap<>();

        return monoProducto.flatMap(producto -> {
            if (producto.getCreateAt() == null) {
                producto.setCreateAt(new Date());
            }
            return productoService.save(producto)
                    .map(p -> {
                        response.put("producto", p);
                        response.put("message", "Producto creado con éxito");
                        response.put("timestamp", new Date());
                        return ResponseEntity.created(URI.create("api/productos/" + p.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response);
                    });
        }).onErrorResume(t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "El campo " + fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(list -> {
                            response.put("errors", list);
                            response.put("timestamp", new Date());
                            response.put("status", HttpStatus.BAD_REQUEST.value());
                            return Mono.just(ResponseEntity.badRequest().body(response));
                        }
                ));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> update(@RequestBody Producto producto, @PathVariable String id) {
        return productoService.findById(id).flatMap(p -> {
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    p.setCategoria(producto.getCategoria());
                    return productoService.save(p);
                }).map(p -> ResponseEntity.created(URI.create("api/productos/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productoService.findById(id)
                .flatMap(p -> productoService.delete(p)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

}
