package no.kristianped.recipemongo.services;

import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.converters.RecipeCommandToRecipe;
import no.kristianped.recipemongo.converters.RecipeToRecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.exceptions.NotFoundException;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm the service");
        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String l) {
        Mono<Recipe> recipeMono = recipeRepository.findById(l);

        try {
            Recipe recipe = recipeMono.block();
            if (recipe == null)
                throw new NullPointerException();
        } catch (Exception e) {
            throw new NotFoundException("Could not find recipe with ID " + l);
        }

        return recipeMono;
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        return recipeRepository.save(recipeCommandToRecipe.convert(command))
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<RecipeCommand> findByCommandById(String id) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                   RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
                   recipeCommand.getIngredients().forEach(rc -> rc.setRecipeId(recipeCommand.getId()));

                   return recipeCommand;
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        try {
            recipeRepository.deleteById(id).block();
        } catch (Exception e) {
            // just catch
        }


        return Mono.empty();
    }
}
