package cl.mmconecta.backend.service;

import cl.mmconecta.backend.model.Contenido;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fuente de datos del catalogo. Por ahora en memoria (igual que los datos
 * ficticios de la app Android); el dia de manana se puede reemplazar por
 * una base de datos real sin tocar el controller.
 */
@Service
public class CatalogoService {

    private final List<Contenido> contenidos = new ArrayList<>();

    public CatalogoService() {
        contenidos.add(new Contenido(
                "1",
                "Introduccion a las masas",
                "Descubre conceptos basicos para preparar tus primeras masas.",
                "Panaderia",
                List.of(
                        "Selecciona harina de buena calidad",
                        "Respeta los tiempos de reposo",
                        "Amasa hasta lograr una textura elastica"
                )
        ));
        contenidos.add(new Contenido(
                "2",
                "Rellenos clasicos de pasteleria",
                "Aprende a preparar cremas y rellenos para tortas y pasteles.",
                "Pasteleria",
                List.of(
                        "Prepara una crema pastelera base",
                        "Incorpora saborizantes naturales",
                        "Deja enfriar antes de usar"
                )
        ));
        contenidos.add(new Contenido(
                "3",
                "Temperado de chocolate",
                "Tecnica basica para lograr brillo y textura en el chocolate.",
                "Chocolateria",
                List.of(
                        "Funde el chocolate a baño maria",
                        "Controla la temperatura con termometro",
                        "Enfria sobre una superficie de marmol"
                )
        ));
    }

    public List<Contenido> listar() {
        return contenidos;
    }

    public List<Contenido> listarPorCategoria(String categoria) {
        if (categoria == null || categoria.isBlank() || categoria.equalsIgnoreCase("Todos")) {
            return contenidos;
        }
        return contenidos.stream()
                .filter(c -> c.getCategory().equalsIgnoreCase(categoria))
                .toList();
    }

    public Optional<Contenido> buscarPorId(String id) {
        return contenidos.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }
}
