package no.kristianped.recipemongo.repositories.reactive;

import no.kristianped.recipemongo.domain.UnitOfMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
class UnitOfMeasureReactiveRepositoryTest {

    @Autowired
    UnitOfMeasureReactiveRepository reactiveRepository;

    @BeforeEach
    void setUp() {
        reactiveRepository.deleteAll().block();
    }

    @Test
    void testSave() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription("Bar");

        reactiveRepository.save(uom).block();
        Long count = reactiveRepository.count().block();

        assertEquals(1L, count);
    }

    @Test
    void testFindByDescription() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription("Bar");

        reactiveRepository.save(uom).block();
        UnitOfMeasure fetchedUom = reactiveRepository.findByDescription("Bar").block();

        assertNotNull(fetchedUom);
        assertNotNull(fetchedUom.getId());
    }
}