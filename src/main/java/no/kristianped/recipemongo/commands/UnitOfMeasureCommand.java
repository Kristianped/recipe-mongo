package no.kristianped.recipemongo.commands;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class UnitOfMeasureCommand {
    String id;
    String description;
}
