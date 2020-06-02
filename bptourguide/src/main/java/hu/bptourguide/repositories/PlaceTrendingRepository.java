package hu.bptourguide.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.PlaceTrending;

@Repository
public interface PlaceTrendingRepository extends CrudRepository<PlaceTrending, Integer> {

}
