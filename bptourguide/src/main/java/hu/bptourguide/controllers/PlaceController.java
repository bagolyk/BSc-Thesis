package hu.bptourguide.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import hu.bptourguide.entities.Category;
import hu.bptourguide.entities.Event;
import hu.bptourguide.entities.Location;
import hu.bptourguide.entities.Place;
import hu.bptourguide.entities.PlaceTrending;
import hu.bptourguide.entities.User;
import hu.bptourguide.repositories.CategoryRepository;
import hu.bptourguide.repositories.LocationRepository;
import hu.bptourguide.repositories.PlaceRepository;
import hu.bptourguide.repositories.PlaceTrendingRepository;
import hu.bptourguide.repositories.UserRepository;
import hu.bptourguide.requestobjects.PlaceRequest;

@CrossOrigin("*")
@RestController
@RequestMapping("/places")
public class PlaceController {
	
	@Autowired
    private PlaceRepository placeRepository;
	
	@Autowired
    private LocationRepository locationRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private PlaceTrendingRepository placeTrendingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	// GET mappings:
	
	@GetMapping("")
    public ResponseEntity<Iterable<Place>> getAll() {
        Iterable<Place> places = placeRepository.findAll();
        return ResponseEntity.ok(places);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Integer id) {
        Optional<Place> oPlace = placeRepository.findById(id);
        if (!oPlace.isPresent()) {
            return ResponseEntity.notFound().build();   
        }
        
        return ResponseEntity.ok(oPlace.get());
    }
	
	@GetMapping("/get-by-name/{name}")
    public ResponseEntity<Place> getPlaceById(@PathVariable String name) {
        Optional<Place> oPlace = placeRepository.findByName(name);
        if (!oPlace.isPresent()) {
            return ResponseEntity.ok(null);   
        }
        return ResponseEntity.ok(oPlace.get());
    }
	
	// POST mappings:
	
	@PostMapping("/new")
	public ResponseEntity<Place> createPlace(@RequestBody PlaceRequest placeReq) {
		Optional<Place> optPlace = placeRepository.findByName(placeReq.getName());
		Location newLocation = placeReq.getLocation();
		Iterable<Location> locations = locationRepository.findByCityAndDistrictAndStreetAndStreetNumber(
				newLocation.getCity(), newLocation.getDistrict(), newLocation.getStreet(), newLocation.getStreetNumber());
		
		Location dbLocation = null;
		for (Location loc: locations) {
			if (loc.equals(placeReq.getLocation())) {
				dbLocation = loc;
			}
		}
		if (dbLocation == null) {
			newLocation.setId(0);
			dbLocation = locationRepository.save(newLocation);
			newLocation = dbLocation;
		}
		
		if( optPlace.isPresent()) { 
			System.out.println("ROSSZ ADAT ERKEZETT!!!!! optPlace.isPresent()");
			return ResponseEntity.badRequest().build();
		}
		if (!placeReq.isPlace()) {
			System.out.println("ROSSZ ADAT ERKEZETT!!!!! !placeReq.isPlace()");
			return ResponseEntity.badRequest().build();
		}

		Place newPlace = new Place(0, placeReq.getName(), placeReq.getDescription(), 
				placeReq.getOpeningHours(), placeReq.getMainCategory(), 
				placeReq.getEmail(), placeReq.getPhoneNum(), placeReq.getTransport(), 
				placeReq.getWebPage(), placeReq.getFacebookPage(), placeReq.getImage(), 
				newLocation, new ArrayList<Event>(), new ArrayList<PlaceTrending>(), 
				new ArrayList<Category>(), new ArrayList<User>());
		
		for (Category category : placeReq.getPlaceCategories()) {
			Optional<Category> optCat = categoryRepository.findByName(category.getName());
			if (optCat.isPresent()) {
				newPlace.addCategory(optCat.get());
			} else if (category.getName() != null) {
				Category newCat = new Category(0, category.getName(), new ArrayList<Place>(), new ArrayList<Event>());
				newPlace.addCategory(categoryRepository.save(newCat));
			} else {
				/*
				System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
				return ResponseEntity.badRequest().build();
				*/
			}
		}
		
		return ResponseEntity.ok(placeRepository.save(newPlace));
	}
	
	// DELETE mappings:
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deletePlace(@PathVariable Integer id) {
		Optional<Place> oPlace = placeRepository.findById(id);
		
		if ( !oPlace.isPresent() ) {
			return ResponseEntity.notFound().build();
		} else if ( !oPlace.get().getEvents().isEmpty() ) {
			return ResponseEntity.badRequest().body("Ez a hely nem törölhető, mert események tartoznak hozzá. Először a hely eseményeit kell törölni, utána lehetséges a hely törlése.");
		}
		Place placeToRemove = oPlace.get();
		
		// First delete the place from the trending and the user saved places

		Iterable<PlaceTrending> placeTrendings = placeTrendingRepository.findAll();
		Iterable<User> users = userRepository.findAll();
		
		for (PlaceTrending pt : placeTrendings) {
			if(pt.getTrendingPlace().equals(placeToRemove)) {
				placeTrendingRepository.delete(pt);
			}
		}
		for (User user : users) {
			if (user.getSavedPlaces().contains(placeToRemove)) {
				user.getSavedPlaces().remove(placeToRemove);
			}
		}
		placeRepository.delete(placeToRemove);
		return ResponseEntity.ok().build();
	}
	
	// PUT mappings:
	
	@PutMapping("/edit/{id}")
	public ResponseEntity<Place> editPlace(@PathVariable Integer id, @RequestBody PlaceRequest placeReq) {
		Optional<Place> oPlace = placeRepository.findById(id);
		Location newLocation = placeReq.getLocation();
		Iterable<Location> locations = locationRepository.findByCityAndDistrictAndStreetAndStreetNumber(
				newLocation.getCity(), newLocation.getDistrict(), newLocation.getStreet(), newLocation.getStreetNumber());
		
		Location dbLocation = null;
		for (Location loc: locations) {
			if (loc.equals(placeReq.getLocation())) {
				dbLocation = loc;
			}
		}
		if (dbLocation == null) {
			newLocation.setId(0);
			dbLocation = locationRepository.save(newLocation);
			newLocation = dbLocation;
		}
		
		if ( !oPlace.isPresent() || !placeReq.isPlace() || !newLocation.isLocation()) {
			return ResponseEntity.badRequest().build();
		}
		Place place = oPlace.get();
		//Location location = oLocation.get();
		
		place.setName(placeReq.getName());
		place.setDescription(placeReq.getDescription());
		place.setOpeningHours(placeReq.getOpeningHours());
		place.setMainCategory(placeReq.getMainCategory());
		place.setEmail(placeReq.getEmail());
		place.setPhoneNum(placeReq.getPhoneNum());
		place.setTransport(placeReq.getTransport());
		place.setWebPage(placeReq.getWebPage());
		place.setFacebookPage(placeReq.getFacebookPage());
		place.setImage(placeReq.getImage());
		place.setLocation(newLocation);
		
		place.getPlaceCategories().clear();
		for (Category category : placeReq.getPlaceCategories()) {
			Optional<Category> optCat = categoryRepository.findByName(category.getName());
			if (optCat.isPresent()) {
				place.addCategory(optCat.get());
			} else if (category.getName() != null) {
				Category newCat = new Category(0, category.getName(), new ArrayList<Place>(), new ArrayList<Event>());
				place.addCategory(categoryRepository.save(newCat));
			} else {
				System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
				return ResponseEntity.badRequest().build();
			}
		}
		
		return ResponseEntity.ok(placeRepository.save(place));
	}
}
