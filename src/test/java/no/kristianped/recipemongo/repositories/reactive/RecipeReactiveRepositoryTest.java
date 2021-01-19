package no.kristianped.recipemongo.repositories.reactive;

import no.kristianped.recipemongo.domain.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class RecipeReactiveRepositoryTest {

    @Autowired
    RecipeReactiveRepository reactiveRepository;

    @BeforeEach
    void setUp() {
        reactiveRepository.deleteAll().block();
    }

    @Test
    void testSave() {
        Recipe recipe = new Recipe();
        reactiveRepository.save(recipe).block();

        Long count = reactiveRepository.count().block();

        assertEquals(1L, count);
    }
}