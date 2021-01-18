package no.kristianped.recipemongo.repositories;

import no.kristianped.recipemongo.bootstrap.RecipeBootstrap;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
class UnitOfMeasureRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository);
        recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    void findUnitOfMeasureByDescription() {
        Optional<UnitOfMeasure> optionalUnitOfMeasure = unitOfMeasureRepository.findUnitOfMeasureByDescription("Teaspoon");

        assertEquals("Teaspoon", optionalUnitOfMeasure.get().getDescription());
    }

    @Test
    void findUnitOfMeasureByDescriptionCup() {
        Optional<UnitOfMeasure> unitOfMeasureOptional = unitOfMeasureRepository.findUnitOfMeasureByDescription("Cup");

        assertTrue(unitOfMeasureOptional.isPresent());
        assertEquals("Cup", unitOfMeasureOptional.get().getDescription());
    }
}