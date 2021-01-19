package no.kristianped.recipemongo.repositories.reactive;

import no.kristianped.recipemongo.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {
}
