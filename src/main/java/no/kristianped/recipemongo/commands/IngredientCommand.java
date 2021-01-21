package no.kristianped.recipemongo.commands;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class IngredientCommand {
    String id;
    String recipeId;

    @NotBlank
    String description;

    @NotNull
    @Min(1)
    BigDecimal amount;

    @NotNull
    UnitOfMeasureCommand unitOfMeasure;
}