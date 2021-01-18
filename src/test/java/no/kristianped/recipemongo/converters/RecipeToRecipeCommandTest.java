package no.kristianped.recipemongo.converters;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeToRecipeCommandTest {

    static final String RECIPE_ID = "1";
    static final Integer COOK_TIME = Integer.valueOf("5");
    static final Integer PREP_TIME = Integer.valueOf("7");
    static final String DESCRIPTION = "My Recipe";
    static final String DIRECTIONS = "Directions";
    static final Difficulty DIFFICULTY = Difficulty.EASY;
    static final Integer SERVINGS = Integer.valueOf("3");
    static final String SOURCE = "Source";
    static final String URL = "Some URL";
    static final String CAT_ID_1 = "1";
    static final String CAT_ID2 = "2";
    static final String INGRED_ID_1 = "3";
    static final String INGRED_ID_2 = "4";
    static final String NOTES_ID = "9";

    RecipeToRecipeCommand converter;

    @BeforeEach
    void setUp() {
        converter = new RecipeToRecipeCommand(new CategoryToCategoryCommand(),
                new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()),
                new NotesToNotesCommand());
    }

    @Test
    void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    void testEmptyObject() {
        assertNotNull(converter.convert(new Recipe()));
    }

    @Test
    void convert() {
        // Given
        Recipe source = new Recipe();
        source.setId(RECIPE_ID);
        source.setCookTime(COOK_TIME);
        source.setPrepTime(PREP_TIME);
        source.setDescription(DESCRIPTION);
        source.setDifficulty(DIFFICULTY);
        source.setDirections(DIRECTIONS);
        source.setServings(SERVINGS);
        source.setSource(SOURCE);
        source.setUrl(URL);

        Notes notes = new Notes();
        notes.setId(NOTES_ID);
        source.setNotes(notes);

        Category category = new Category();
        category.setId(CAT_ID_1);
        source.getCategories().add(category);

        Category category1 = new Category();
        category1.setId(CAT_ID2);
        source.getCategories().add(category1);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(INGRED_ID_1);
        source.getIngredients().add(ingredient);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(INGRED_ID_2);
        source.getIngredients().add(ingredient1);

        // when
        RecipeCommand recipe = converter.convert(source);

        // then
        assertNotNull(recipe);
        assertEquals(RECIPE_ID, recipe.getId());
        assertEquals(COOK_TIME, recipe.getCookTime());
        assertEquals(PREP_TIME, recipe.getPrepTime());
        assertEquals(DESCRIPTION, recipe.getDescription());
        assertEquals(DIFFICULTY, recipe.getDifficulty());
        assertEquals(DIRECTIONS, recipe.getDirections());
        assertEquals(SERVINGS, recipe.getServings());
        assertEquals(SOURCE, recipe.getSource());
        assertEquals(URL, recipe.getUrl());
        assertEquals(NOTES_ID, recipe.getNotes().getId());
        assertEquals(2, recipe.getCategories().size());
        assertEquals(2, recipe.getIngredients().size());
    }
}