package hu.bptourguide.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.bptourguide.entities.Location;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {

	Iterable<Location> findByCityAndDistrictAndStreetAndStreetNumber(String city, String district, String street, String streetNumber);
	 
}
