package dev.velasquez.webflux.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.velasquez.webflux.api.models.documents.Categoria;
import dev.velasquez.webflux.api.models.documents.Producto;
import dev.velasquez.webflux.api.models.service.ProductoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Para probar con un servidor real corriendo en un puerto aleatorio:
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 *
 * Para ejecutar en un ambiente simulado debemos cambar a:
 * @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
 * @AutoConfigureWebTestClient
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebTestClient
class SpringBootWebfluxApirestApplicationTests {


    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductoService productoService;

    @Value("${config.base.endpoint}")
    private String baseUrl;

    @Test
    void listarTest() {
        webTestClient.get()
                .uri(baseUrl)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Producto.class)
//                .hasSize(9)
                .consumeWith(response -> {
                    List<Producto> productos = response.getResponseBody();
                    assert productos != null;
                    productos.forEach(p -> System.out.println(p.getNombre()));
//                    Assertions.assertEquals(9, productos.size());
                    Assertions.assertFalse(productos.isEmpty());
                });
    }

    @Test
    void detalleTest() {
        Producto producto = productoService.findByNombre("Sony Camara HD Digital")
                .block();

        webTestClient.get()
                .uri(baseUrl.concat("/{id}"), producto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)
                /*
               .expectBody()
               .jsonPath("$.id").isNotEmpty()
               .jsonPath("$.nombre").isEqualTo("Sony Camara HD Digital")
               .jsonPath("$.precio").isEqualTo(177.89)
               */
                .consumeWith(response -> {
                    Producto p = response.getResponseBody();
                    assert p != null;
                    Assertions.assertEquals("Sony Camara HD Digital", p.getNombre());
                    Assertions.assertFalse(p.getNombre().isEmpty());
                });
    }

    @Test
    void detalle2Test() {
        Producto producto = productoService.findByNombre("Sony Camara HD Digital")
                .block();

        webTestClient.get()
                .uri(baseUrl.concat("/{id}"), producto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Sony Camara HD Digital")
                .jsonPath("$.precio").isEqualTo(177.89);
    }

    @Test
//    @Disabled("Este text es solo para la version dos, con Router Function")
    public void saveTest() {

        Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
        Producto producto = new Producto("Xbox", 456.89, categoria);

        webTestClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON) // mediaType del request
                .accept(MediaType.APPLICATION_JSON) // mediaType del response
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo(producto.getNombre())
                .jsonPath("$.precio").isEqualTo(producto.getPrecio())
                .jsonPath("$.categoria.id").isEqualTo(categoria.getId());

    }
    @Test
    @Disabled("Este text es solo para la version uno, con restControler")
    public void saveV1Test() {

        Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
        Producto producto = new Producto("Xbox", 456.89, categoria);

        webTestClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON) // mediaType del request
                .accept(MediaType.APPLICATION_JSON) // mediaType del response
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.producto.id").isNotEmpty()
                .jsonPath("$.producto.nombre").isEqualTo(producto.getNombre())
                .jsonPath("$.producto.precio").isEqualTo(producto.getPrecio())
                .jsonPath("$.producto.categoria.id").isEqualTo(categoria.getId());

    }

    @Test
//    @Disabled("Este text es solo para la version dos, con Router Function")
    public void save2Test() {

        Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
        Producto producto = new Producto("Xbox", 456.89, categoria);

        webTestClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON) // mediaType del request
                .accept(MediaType.APPLICATION_JSON) // mediaType del response
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto p = response.getResponseBody();
                    assert p != null;
                    Assertions.assertEquals("Xbox", p.getNombre());
                    Assertions.assertFalse(p.getNombre().isEmpty());
                    Assertions.assertEquals(p.getNombre(), producto.getNombre());
                    Assertions.assertEquals(p.getPrecio(), producto.getPrecio());
                    Assertions.assertEquals(p.getCategoria().getNombre(), categoria.getNombre());
                    Assertions.assertEquals(p.getCategoria().getId(), categoria.getId());
                });
    }

    @Test
    @Disabled("Este text es solo para la version uno, con restControler")
    public void save2V1Test() {

        Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
        Producto producto = new Producto("Xbox", 456.89, categoria);

        webTestClient.post()
                .uri(baseUrl)
                .contentType(MediaType.APPLICATION_JSON) // mediaType del request
                .accept(MediaType.APPLICATION_JSON) // mediaType del response
                .body(Mono.just(producto), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {})
                .consumeWith(response -> {
                    Object o = response.getResponseBody().get("producto");
                    Producto p = new ObjectMapper().convertValue(o, Producto.class);
                    assert p != null;
                    Assertions.assertEquals("Xbox", p.getNombre());
                    Assertions.assertFalse(p.getNombre().isEmpty());
                    Assertions.assertEquals(p.getNombre(), producto.getNombre());
                    Assertions.assertEquals(p.getPrecio(), producto.getPrecio());
                    Assertions.assertEquals(p.getCategoria().getNombre(), categoria.getNombre());
                    Assertions.assertEquals(p.getCategoria().getId(), categoria.getId());
                });
    }

    @Test
    public void updateTest() {
        Producto producto = productoService.buscarPorNombre("Bianchi Bicicleta").block();
        Categoria categoria = productoService.findCategoriaByNombre("Muebles").block();
        Producto productoEditado = new Producto("Xbox", 456.89, categoria);
        assert producto != null;
        webTestClient.put()
                .uri(baseUrl.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
                .contentType(MediaType.APPLICATION_JSON) // mediaType del request
                .accept(MediaType.APPLICATION_JSON) // mediaType del response
                .body(Mono.just(productoEditado), Producto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo(productoEditado.getNombre())
                .jsonPath("$.categoria.nombre").isEqualTo(productoEditado.getCategoria().getNombre());

    }

    @Test
    public void deleteTest() {
        Producto producto = productoService.buscarPorNombre("Apple iPod").block();

        webTestClient.delete()
                .uri(baseUrl.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        webTestClient.get()
                .uri(baseUrl.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }
}
