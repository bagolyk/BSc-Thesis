package hu.bptourguide.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.Event;

import hu.bptourguide.entities.Place;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {
	
	Iterable<Event> findByPlace(Place place);
	
	Optional<Event> findByName(String name);

}