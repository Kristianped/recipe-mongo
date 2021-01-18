package no.kristianped.recipemongo.repositories;

import no.kristianped.recipemongo.domain.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, String> {
}
