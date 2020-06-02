package hu.bptourguide.controllers;

import java.util.ArrayList;
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

import hu.bptourguide.entities.Category;
import hu.bptourguide.entities.Event;
import hu.bptourguide.entities.EventTrending;
import hu.bptourguide.entities.Place;
import hu.bptourguide.entities.User;
import hu.bptourguide.repositories.CategoryRepository;
import hu.bptourguide.repositories.EventRepository;
import hu.bptourguide.repositories.EventTrendingRepository;
import hu.bptourguide.repositories.PlaceRepository;
import hu.bptourguide.repositories.UserRepository;
import hu.bptourguide.requestobjects.EventRequest;

@CrossOrigin("*")
@RestController
@RequestMapping("/events")
public class EventController {
	
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private PlaceRepository placeRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EventTrendingRepository eventTrendingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	//GET mapping:
	
	@GetMapping("")
    public ResponseEntity<Iterable<Event>> getAll() {
        Iterable<Event> events = eventRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(events);
	}
	
	@GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (!event.isPresent()) {
            return ResponseEntity.notFound().build();   
        }
        return ResponseEntity.status(HttpStatus.OK).body(event.get());
    }
	
	@GetMapping("/get-by-name/{name}")
    public ResponseEntity<Event> getEventById(@PathVariable String name) {
        Optional<Event> oEvent = eventRepository.findByName(name);
        if (!oEvent.isPresent()) {
            return ResponseEntity.ok(null);   
        }
        return ResponseEntity.ok(oEvent.get());
    }
	
	// POST mappings:
	
	@PostMapping("/new")
	public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventReq) {
		Optional<Place> optPlace = placeRepository.findById(eventReq.getPlaceId());
		
		if( !eventReq.isEvent() || !optPlace.isPresent() ) { 
			System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
			return ResponseEntity.badRequest().build();
		}
		
		System.out.println(eventReq);

		Event newEvent = new Event(0, eventReq.getName(), eventReq.getDescription(), 
									eventReq.getStartDate(), eventReq.getEndDate(), eventReq.getMainCategory(), 
									eventReq.getWebPage(), eventReq.getFacebookLink(), 
									eventReq.getImage(), optPlace.get(), 
									new ArrayList<EventTrending>(), 
									new ArrayList<Category>(), new ArrayList<User>() );
		
		for (Category category : eventReq.getEventCategories()) {
			Optional<Category> optCat = categoryRepository.findByName(category.getName());
			if (optCat.isPresent()) {
				newEvent.addCategory(optCat.get());
			} else if (category.getName() != null) {
				Category newCat = new Category(0, category.getName(), new ArrayList<Place>(), new ArrayList<Event>());
				newEvent.addCategory(categoryRepository.save(newCat));
			} else {
				System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
				return ResponseEntity.badRequest().build();
			}
		}
		
		return ResponseEntity.ok(eventRepository.save(newEvent));
	}
	
	// DELETE mappings:
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deleteEvent(@PathVariable Integer id) {
		Optional<Event> oEvent = eventRepository.findById(id);
		if ( !oEvent.isPresent() ) {
			return ResponseEntity.notFound().build();
		}
		Event eventToRemove = oEvent.get();
		
		// First delete the event from the trending and the user saved events
		Iterable<EventTrending> eventTrendings = eventTrendingRepository.findAll();
		Iterable<User> users = userRepository.findAll();
		
		Place place = eventToRemove.getPlace();
		place.getEvents().remove(eventToRemove);
		
		for (EventTrending et : eventTrendings) {
			if(et.getTrendingEvent().equals(eventToRemove)) {
				eventTrendingRepository.delete(et);
			}
		}
		for (User user : users) {
			if (user.getSavedEvents().contains(eventToRemove)) {
				user.getSavedEvents().remove(eventToRemove);
			}
		}
		eventRepository.delete(eventToRemove);
		return ResponseEntity.ok().build();
	}
	
	//PUT mappings:
	
	@PutMapping("/edit/{id}")
	public ResponseEntity<Event> editEvent(@PathVariable Integer id, @RequestBody EventRequest eventReq) {
		if (eventReq.getPlaceId() == null) {
			System.out.println("Rossz adat érkezett!");
			return ResponseEntity.notFound().build();
		}
		Optional<Event> oEvent = eventRepository.findById(id);
		Optional<Place> oPlace = placeRepository.findById(eventReq.getPlaceId());
		if ( !oEvent.isPresent() || !oPlace.isPresent() || !eventReq.isEvent() ) {
			return ResponseEntity.notFound().build();
		}
		Event event = oEvent.get();
		Place place = oPlace.get();
		
		event.setName(eventReq.getName());
		event.setDescription(eventReq.getDescription());
		event.setStartDate(eventReq.getStartDate());
		event.setEndDate(eventReq.getEndDate());
		event.setMainCategory(eventReq.getMainCategory());
		event.setWebPage(eventReq.getWebPage());
		event.setFacebookLink(eventReq.getFacebookLink());
		event.setImage(eventReq.getImage());
		event.setPlace(place);
		event.getEventCategories().clear();
		for (Category category : eventReq.getEventCategories()) {
			Optional<Category> optCat = categoryRepository.findByName(category.getName());
			if (optCat.isPresent()) {
				event.addCategory(optCat.get());
			} else if (category.getName() != null) {
				Category newCat = new Category(0, category.getName(), new ArrayList<Place>(), new ArrayList<Event>());
				event.addCategory(categoryRepository.save(newCat));
			} else {
				System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
				return ResponseEntity.badRequest().build();
			}
		}
		
		return ResponseEntity.ok(eventRepository.save(event));
	}
}
