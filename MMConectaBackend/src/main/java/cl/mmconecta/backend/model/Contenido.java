package cl.mmconecta.backend.model;

import java.util.List;

/**
 * Representa un contenido del catalogo (receta/curso) de MMConecta.
 * Misma forma que el modelo LearningContent de la app Android, para que
 * Retrofit pueda mapear la respuesta JSON directamente.
 */
public class Contenido {

    private String id;
    private String title;
    private String description;
    private String category;
    private List<String> steps;

    public Contenido() {
    }

    public Contenido(String id, String title, String description, String category, List<String> steps) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
