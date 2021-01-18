package no.kristianped.recipemongo.converters;

import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.domain.Ingredient;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IngredientToIngredientCommandTest {

    private static final Recipe RECIPE = new Recipe();
    private static final BigDecimal AMOUNT = new BigDecimal("1");
    private static final String ID_VALUE = "1";
    private static final String UOM_VALUE = "2";
    private static final String DESCRIPTION = "Cheesebuger";

    IngredientToIngredientCommand converter;

    @BeforeEach
    void setUp() {
        converter = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
    }

    @Test
    void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    void testEmptyObject() {
        assertNotNull(converter.convert(new Ingredient()));
    }

    @Test
    void convert() {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setAmount(AMOUNT);
        ingredient.setDescription(DESCRIPTION);
        ingredient.setId(ID_VALUE);
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId(UOM_VALUE);
        ingredient.setUnitOfMeasure(uom);

        // when
        IngredientCommand ingredientCommand = converter.convert(ingredient);

        // then
        assertNotNull(ingredientCommand);
        assertNotNull(ingredientCommand.getUnitOfMeasure());
        assertEquals(ID_VALUE, ingredientCommand.getId());
        assertEquals(UOM_VALUE, ingredientCommand.getUnitOfMeasure().getId());
        assertEquals(DESCRIPTION, ingredientCommand.getDescription());
        assertEquals(AMOUNT, ingredientCommand.getAmount());
    }

    @Test
    void testWithNullUom() {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setAmount(AMOUNT);
        ingredient.setDescription(DESCRIPTION);
        ingredient.setId(ID_VALUE);

        // when
        IngredientCommand ingredientCommand = converter.convert(ingredient);

        // then
        assertNotNull(ingredientCommand);
        assertNull(ingredientCommand.getUnitOfMeasure());
        assertEquals(ID_VALUE, ingredientCommand.getId());
        assertEquals(DESCRIPTION, ingredientCommand.getDescription());
        assertEquals(AMOUNT, ingredientCommand.getAmount());
    }
}