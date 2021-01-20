package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;
import no.kristianped.recipemongo.converters.UnitOfMeasureToUnitOfMeasureCommand;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import no.kristianped.recipemongo.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitOfMeasureServiceImplTest {

    UnitOfMeasureToUnitOfMeasureCommand converter = new UnitOfMeasureToUnitOfMeasureCommand();
    UnitOfMeasureService service;

    @Mock
    UnitOfMeasureReactiveRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UnitOfMeasureServiceImpl(repository, converter);
    }

    @Test
    void listAllUoms() {
        // given
        Set<UnitOfMeasure> set = new HashSet<>();
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId("1");
        set.add(uom1);

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId("2");
        set.add(uom2);

        // when
        Mockito.when(repository.findAll()).thenReturn(Flux.just(uom1, uom2));
        List<UnitOfMeasureCommand> commands = service.listAllUoms().collectList().block();

        // then
        assertEquals(2, commands.size());
        Mockito.verify(repository, Mockito.times(1)).findAll();
    }
}