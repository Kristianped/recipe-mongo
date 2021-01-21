package no.kristianped.recipemongo.commands;

import lombok.*;
import lombok.experimental.FieldDefaults;
import no.kristianped.recipemongo.domain.Difficulty;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class RecipeCommand {
    String id;

    @NotBlank
    @Size(min = 3, max = 255)
    String description;

    @Min(1)
    @Max(999)
    Integer prepTime;

    @Min(1)
    @Max(999)
    Integer cookTime;

    @Min(1)
    @Max(100)
    Integer servings;
    String source;

    @URL
    String url;

    @NotBlank
    String directions;

    List<IngredientCommand> ingredients = new ArrayList<>();
    byte[] image;
    Difficulty difficulty;
    NotesCommand notes;
    List<CategoryCommand> categories = new ArrayList<>();
}