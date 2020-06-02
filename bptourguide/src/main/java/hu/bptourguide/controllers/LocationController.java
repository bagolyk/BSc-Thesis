package hu.bptourguide.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.bptourguide.entities.Location;
import hu.bptourguide.entities.Place;
import hu.bptourguide.repositories.LocationRepository;
import hu.bptourguide.repositories.PlaceRepository;
import hu.bptourguide.requestobjects.LocationRequest;

@CrossOrigin("*")
@RestController
@RequestMapping("/locations")
public class LocationController {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PlaceRepository placeRepository;

	// GET mapping:

	@GetMapping("")
	public ResponseEntity<Iterable<Location>> getAll() {
		Iterable<Location> locations = locationRepository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(locations);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
		Optional<Location> location = locationRepository.findById(id);
		if (!location.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.status(HttpStatus.OK).body(location.get());
	}

	// POST mappings:

	@PostMapping("/new")
	public ResponseEntity<Location> createLocation(@RequestBody LocationRequest locationReq) {
		Iterable<Location> locations = locationRepository.findByCityAndDistrictAndStreetAndStreetNumber(
				locationReq.getCity(), locationReq.getDistrict(), locationReq.getStreet(),
				locationReq.getStreetNumber());

		if (!locationReq.isLocation()) {
			return ResponseEntity.badRequest().build();
		}

		Location newLocation = new Location(0, locationReq.getPostcode(), locationReq.getCity(),
				locationReq.getDistrict(), locationReq.getStreet(), locationReq.getTypeOfStreet(),
				locationReq.getStreetNumber(), locationReq.getStairWay(), locationReq.getFloor(),
				locationReq.getDoor());

		for (Location l : locations) {
			if (newLocation.equals(l)) {
				return ResponseEntity.ok(l);
			}
		}

		return ResponseEntity.ok(locationRepository.save(newLocation));
	}

	// DELETE mappings:

	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deleteLocation(@PathVariable Integer id) {
		Optional<Location> oLocation = locationRepository.findById(id);
		if (!oLocation.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Location locToRemove = oLocation.get();
		Iterable<Place> places = placeRepository.findAll();

		// In this use case it doesn't allow to remove location if it's part of a place,
		// because a place should have a location, and it can have only one.
		// Places and events could exist without categories, so in that case it's
		// allowed to remove a category from a place/event.

		for (Place place : places) {
			if (place.getLocation().getId() == locToRemove.getId()) {
				return ResponseEntity.badRequest().body("Location can't be deleted since it is added to a place.");
			}
		}
		locationRepository.delete(locToRemove);
		return ResponseEntity.ok().build();
	}

	// PUT mappings:

	@PutMapping("/edit/{id}")
	public ResponseEntity<Location> editLocation(@PathVariable Integer id, @RequestBody LocationRequest locReq) {
		Optional<Location> oLocation = locationRepository.findById(id);
		if ( !oLocation.isPresent() ) {
			return ResponseEntity.notFound().build();
		}
		Location location = oLocation.get();
		
		if (locReq.getCity() != null)
			location.setCity(locReq.getCity());
		
		location.setPostcode(locReq.getPostcode());
		location.setDistrict(locReq.getDistrict());
		location.setStreet(locReq.getStreet());
		location.setTypeOfStreet(locReq.getTypeOfStreet());
		location.setStreetNumber(locReq.getStreetNumber());
		location.setStairWay(locReq.getStairWay());
		location.setFloor(locReq.getFloor());
		location.setDoor(locReq.getDoor());
		
		return ResponseEntity.ok(locationRepository.save(location));
	}

}
