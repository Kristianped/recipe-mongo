package no.kristianped.recipemongo.converters;

import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasureToUnitOfMeasureCommand implements Converter<UnitOfMeasure, UnitOfMeasureCommand> {

    @Nullable
    @Override
    public UnitOfMeasureCommand convert(UnitOfMeasure source) {
        if (source == null)
            return null;

        final UnitOfMeasureCommand unit = new UnitOfMeasureCommand();
        unit.setId(source.getId());
        unit.setDescription(source.getDescription());

        return unit;
    }
}
