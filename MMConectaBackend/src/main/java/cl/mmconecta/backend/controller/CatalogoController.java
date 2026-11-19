package cl.mmconecta.backend.controller;

import cl.mmconecta.backend.model.Contenido;
import cl.mmconecta.backend.service.CatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    // GET /api/catalogo              -> todo el catalogo
    // GET /api/catalogo?categoria=Panaderia -> filtrado por categoria
    @GetMapping
    public List<Contenido> listar(@RequestParam(required = false) String categoria) {
        return catalogoService.listarPorCategoria(categoria);
    }

    // GET /api/catalogo/{id} -> detalle de un contenido
    @GetMapping("/{id}")
    public ResponseEntity<Contenido> detalle(@PathVariable String id) {
        return catalogoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
