package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.converters.*;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.exceptions.NotFoundException;
import no.kristianped.recipemongo.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    void getRecipeByIdNotFound() {
        Optional<Recipe> recipeOptional = Optional.empty();

        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

        assertThrows(NotFoundException.class, () -> {
            Recipe recipeReturned = recipeService.findById("1");
        });
    }

    @Test
    void getRecipeById() {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

        Recipe recipeReturned = recipeService.findById("1");

        assertNotNull(recipeReturned);
        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, never()).findAll();
    }

    @Test
    void getRecipes() {
        Recipe recipe = new Recipe();
        HashSet<Recipe> recipesData = new HashSet<>();
        recipesData.add(recipe);

        when(recipeRepository.findAll()).thenReturn(recipesData);

        Set<Recipe> recipes = recipeService.getRecipes();

        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    void testDeleteById() {
        // given
        String idToDelete = "2";
        recipeService.deleteById(idToDelete);

        // no when, delete is void

        // then
        verify(recipeRepository, times(1)).deleteById(anyString());
    }

    @Test
    void testFindByCommandId() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Optional<Recipe> optionalRecipe = Optional.of(recipe);

        recipeToRecipeCommand = new RecipeToRecipeCommand(new CategoryToCategoryCommand(), new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()), new NotesToNotesCommand());
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);

        // when
        when(recipeRepository.findById(anyString())).thenReturn(optionalRecipe);
        RecipeCommand command = recipeService.findByCommandById("1");

        // then
        assertNotNull(command);
        assertEquals(command.getId(), recipe.getId());
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
        when(recipeRepository.save(any())).thenReturn(recipe);

        // then
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(recipeCommand);
        assertNotNull(savedCommand);
        assertEquals(savedCommand.getId(), recipeCommand.getId());
    }
}