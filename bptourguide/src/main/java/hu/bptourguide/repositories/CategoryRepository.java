package hu.bptourguide.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer>{

	Optional<Category> findByName(String name);

}
