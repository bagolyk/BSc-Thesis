package hu.bptourguide.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.EventTrending;

@Repository
public interface EventTrendingRepository extends CrudRepository<EventTrending, Integer> {

}
