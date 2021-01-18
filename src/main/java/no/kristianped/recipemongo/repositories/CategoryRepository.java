package no.kristianped.recipemongo.repositories;

import no.kristianped.recipemongo.domain.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, String> {

    Optional<Category> findCategoryByDescription(String description);
}
