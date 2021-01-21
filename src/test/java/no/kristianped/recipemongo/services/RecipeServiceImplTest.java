package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.converters.*;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @InjectMocks
    RecipeServiceImpl recipeService;

    @Test
    void getRecipeById() {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        StepVerifier.create(recipeService.findById("1"))
                .expectNext(recipe)
                .expectComplete()
                .verify();

        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    void testFindByCommandId() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        RecipeCommand command = new RecipeCommand();
        command.setId("1");

        // when
        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeToRecipeCommand.convert(any())).thenReturn(command);

        StepVerifier.create(recipeService.findByCommandById(command.getId()))
                .expectNext(command)
                .expectComplete()
                .verify();

        // then
        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    void getRecipes() {
        Recipe recipe = new Recipe();
        HashSet<Recipe> recipesData = new HashSet<>();
        recipesData.add(recipe);
        Flux<Recipe> recipeFlux = Flux.just(recipe);

        when(recipeRepository.findAll()).thenReturn(recipeFlux);

        List<Recipe> recipes = recipeService.getRecipes().collectList().block();

        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById() {
        // given
        String idToDelete = "2";

        // no when, delete is void
        when(recipeRepository.deleteById(anyString())).thenReturn(Mono.empty());
        StepVerifier.create(recipeService.deleteById(idToDelete))
                .expectComplete()
                .verify();

        // then
        verify(recipeRepository, times(1)).deleteById(anyString());
    }



    @Test
    void testSaveRecipeCommand() {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        Recipe recipe = new Recipe();
        recipe.setId("1");

        recipeToRecipeCommand = new RecipeToRecipeCommand(new CategoryToCategoryCommand(), new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()), new NotesToNotesCommand());
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);

        // when
        when(recipeCommandToRecipe.convert(any())).thenReturn(recipe);
        when(recipeRepository.save(any())).thenReturn(Mono.just(recipe));

        // then
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(recipeCommand).block();
        assertNotNull(savedCommand);
        assertEquals(savedCommand.getId(), recipeCommand.getId());
    }
}