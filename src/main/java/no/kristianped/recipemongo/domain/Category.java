package no.kristianped.recipemongo.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"recipes"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document
public class Category {

    @Id
    String id;
    String description;
    Set<Recipe> recipes = new HashSet<>();
}
