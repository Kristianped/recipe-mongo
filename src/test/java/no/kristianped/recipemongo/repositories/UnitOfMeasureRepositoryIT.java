package no.kristianped.recipemongo.repositories;

import no.kristianped.recipemongo.domain.UnitOfMeasure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@DataJpaTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findUnitOfMeasureByDescription() {
        Optional<UnitOfMeasure> unitOfMeasureOptional = unitOfMeasureRepository.findUnitOfMeasureByDescription("Teaspoon");

        assertTrue(unitOfMeasureOptional.isPresent());
        assertEquals("Teaspoon", unitOfMeasureOptional.get().getDescription());
    }

    @Test
    void findUnitOfMeasureByDescriptionCup() {
        Optional<UnitOfMeasure> unitOfMeasureOptional = unitOfMeasureRepository.findUnitOfMeasureByDescription("Cup");

        assertTrue(unitOfMeasureOptional.isPresent());
        assertEquals("Cup", unitOfMeasureOptional.get().getDescription());
    }
}