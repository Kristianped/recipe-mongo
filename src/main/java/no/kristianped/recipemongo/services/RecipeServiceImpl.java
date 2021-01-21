package no.kristianped.recipemongo.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.converters.RecipeCommandToRecipe;
import no.kristianped.recipemongo.converters.RecipeToRecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    RecipeReactiveRepository recipeRepository;
    RecipeCommandToRecipe recipeCommandToRecipe;
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm the service");
        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String l) {
        return recipeRepository.findById(l);
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        return recipeRepository.save(recipeCommandToRecipe.convert(command))
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<RecipeCommand> findByCommandById(String id) {
        return recipeRepository.findById(id)
                .map(recipeToRecipeCommand::convert)
                .doOnNext(recipeCommand -> recipeCommand.getIngredients().forEach(ingredient -> ingredient.setRecipeId(recipeCommand.getId())));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return recipeRepository.deleteById(id).then();
    }
}
