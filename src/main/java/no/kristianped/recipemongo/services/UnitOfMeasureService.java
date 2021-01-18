package no.kristianped.recipemongo.services;


import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;

import java.util.Set;

public interface UnitOfMeasureService {

    Set<UnitOfMeasureCommand> listAllUoms();
}
