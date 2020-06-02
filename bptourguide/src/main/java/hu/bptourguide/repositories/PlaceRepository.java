package hu.bptourguide.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, Integer> {

	Optional<Place> findByName(String name);
	
}
