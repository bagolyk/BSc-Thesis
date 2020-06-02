package hu.bptourguide.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import hu.bptourguide.entities.Place;
import hu.bptourguide.repositories.CategoryRepository;
import hu.bptourguide.repositories.EventRepository;
import hu.bptourguide.repositories.PlaceRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("/categories")
public class CategoryController {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private PlaceRepository placeRepository;
	
	//GET mappings:
	
	@GetMapping("")
    public ResponseEntity<Iterable<Category>> getAll() {
        Iterable<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            return ResponseEntity.notFound().build();   
        }
        
        return ResponseEntity.status(HttpStatus.OK).body(category.get());
    }
	
	@GetMapping("/getbyname")
	public ResponseEntity<Category> getCategoryByName(@RequestBody String name) {
		Optional<Category> category = categoryRepository.findByName(name);
        if (!category.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.status(HttpStatus.OK).body(category.get());
	}
	
	// POST mappings:
	
	@PostMapping("/new")
	public ResponseEntity<Category> createCategory(@RequestBody String category, Authentication auth) {
		String userName = auth.getName();
		if (!userName.equals("admin")) {
			return ResponseEntity.status(403).body(null);
		}
		Optional<Category> optCat = categoryRepository.findByName(category);
		if ( optCat.isPresent() || category == null ) {
			System.out.println("ROSSZ ADAT ERKEZETT!!!!!");
			return ResponseEntity.badRequest().build();
		}
		Category newCategory = new Category(0, category, new ArrayList<Place>(), new ArrayList<Event>());
		return ResponseEntity.ok(categoryRepository.save(newCategory));
	}
	
	// DELETE mappings:
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity deleteCategory(@PathVariable Integer id, Authentication auth) {
		System.out.println(auth.toString());
		String userName = auth.getName();
		System.out.println(userName.toString());
		if (!userName.equals("admin")) {
			return ResponseEntity.status(403).body(null);
		}
		
		Optional<Category> oCategory = categoryRepository.findById(id);
		if ( !oCategory.isPresent() ) {
			return ResponseEntity.notFound().build();
		}
		Category catToRemove = oCategory.get();
		
		// First delete the category from the events and the places
		
		Iterable<Place> places = placeRepository.findAll();
		Iterable<Event> events = eventRepository.findAll();
		for (Place place : places) {
			if (place.getPlaceCategories().contains(catToRemove)) {
				place.getPlaceCategories().remove(catToRemove);
			}
		}
		for (Event event : events) {
			if (event.getEventCategories().contains(catToRemove)) {
				event.getEventCategories().remove(catToRemove);
			}
		}
		categoryRepository.delete(catToRemove);
		return ResponseEntity.ok().build();
	}
	
	// PUT mappings:
	
	@PutMapping("/edit/{id}")
	public ResponseEntity<Category> renameCategory(@PathVariable Integer id, @RequestBody String name) {
		Optional<Category> oCategory = categoryRepository.findById(id);
		if ( !oCategory.isPresent() || name == null ) {
			return ResponseEntity.notFound().build();
		}
		
		Category category = oCategory.get();
		category.setName(name);
		return ResponseEntity.ok(categoryRepository.save(category));
	} 
	
}
