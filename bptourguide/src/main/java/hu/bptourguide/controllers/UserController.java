package hu.bptourguide.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.bptourguide.entities.Event;
import hu.bptourguide.entities.User;
import hu.bptourguide.entities.Place;
import hu.bptourguide.repositories.EventRepository;
import hu.bptourguide.repositories.PlaceRepository;
import hu.bptourguide.repositories.UserRepository;
import hu.bptourguide.requestobjects.EventRequest;
import hu.bptourguide.requestobjects.PlaceRequest;
import hu.bptourguide.requestobjects.UserRequest;
import hu.bptourguide.requestobjects.UserWithRole;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
public class UserController {

		@Autowired
		private UserRepository userRepository;
		
		@Autowired
		private PlaceRepository placeRepository;
		
		@Autowired
		private EventRepository eventRepository;
		
		org.springframework.security.crypto.password.PasswordEncoder passwordEncoder
		   = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

		// GET mappings:
		
		@GetMapping("")
	    public ResponseEntity<Iterable<User>> getAll() {
	        Iterable<User> users = userRepository.findAll();
	        return ResponseEntity.ok(users);
	    }
		
		@GetMapping("/{id}")
	    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
	        Optional<User> user = userRepository.findById(id);
	        if (!user.isPresent()) {
	            return ResponseEntity.notFound().build();   
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(user.get());
	    }
		
		@GetMapping("exists-with-username/{username}")
		public ResponseEntity<Boolean> userNameExists(@PathVariable String username) {
			return ResponseEntity.ok(userRepository.existsByUsername(username));
		}
		
		@GetMapping("exists-with-email/{email}")
		public ResponseEntity<Boolean> emailExists(@PathVariable String email) {
			return ResponseEntity.ok(userRepository.existsByEmail(email));
		}
		
		// TODO
		@GetMapping("/get-byemail")
		public ResponseEntity<User> getUserByEmail(@RequestBody String email) {
			Optional<User> user = userRepository.findByEmail(email);	
			if (!user.isPresent()) {
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(user.get());
		}
		
		// POST mappings:
		
		@PostMapping("/new")
	    public ResponseEntity<User> createUser(@RequestBody UserRequest userReq) {
	        if(!userReq.isUser()) {
	        	return ResponseEntity.badRequest().build();
	        }
	        String hashPassword = passwordEncoder.encode(userReq.getPassword());
	        User newUser = new User(0, userReq.getUsername(), userReq.getEmail(), hashPassword, "USER", new ArrayList<Place>(), new ArrayList<Event>());
	        // User newUser = new User(0, userReq.getUsername(), userReq.getEmail(), passwordEncoder.encode(user.getPassword()), new ArrayList<Place>(), new ArrayList<Event>());        
	        return ResponseEntity.ok(userRepository.save(newUser));
	    }
		
		@SuppressWarnings("rawtypes")
		@PostMapping("/login")
	    public ResponseEntity login(@RequestBody UserRequest userReq) {
	        Optional<User> oUser = userRepository.findByUsername(userReq.getUsername());
	        if (!oUser.isPresent()) {
	            return ResponseEntity.status(401).body("User not found");
	        }
	        if (!passwordEncoder.matches(userReq.getPassword(), oUser.get().getPassword())) {
	        	return ResponseEntity.status(403).body("Incorrect password");
	        }
	        return ResponseEntity.ok(oUser.get());
	    }
		
		// DELETE mappings:
		
		@SuppressWarnings("rawtypes")
		@DeleteMapping("/delete/{id}")
		public ResponseEntity deleteUser(@PathVariable Integer id) {
			Optional<User> oUser = userRepository.findById(id);
	        if ( !oUser.isPresent() ) {
	            return ResponseEntity.notFound().build();   
	        }
			User userToRemove = oUser.get();
			
			userRepository.delete(userToRemove);
			return ResponseEntity.ok().build();
		}
		
		@PutMapping("/edit/{id}")
		public ResponseEntity<User> editUser(@PathVariable Integer id, @RequestBody UserRequest userReq) {
			Optional<User> oUser = userRepository.findById(id);
	        if (!oUser.isPresent()) {
	            return ResponseEntity.notFound().build();   
	        }
	        User user = oUser.get();
	        String hashPassword = passwordEncoder.encode(userReq.getPassword());
	        
	        if (userReq.getUsername() != null)
	        	user.setUsername(userReq.getUsername());
	        if (userReq.getPassword() != null)
	        	user.setPassword(hashPassword);
	        
	        return ResponseEntity.ok(userRepository.save(user));
		}
		
		@PutMapping("/{id}/save-event")
		public ResponseEntity<User> addSavedEventToUser(@PathVariable Integer id, @RequestBody Integer eventId) {
			Optional<User> oUser = userRepository.findById(id);
	        if (!oUser.isPresent()) {
	            return ResponseEntity.notFound().build();   
	        }
	        User user = oUser.get();
	        
	        Optional<Event> oEvent = eventRepository.findById(eventId);
	        if (!oEvent.isPresent()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        if (!user.getSavedEvents().contains(oEvent.get())) {
	        	user.getSavedEvents().add(oEvent.get());
	        }
	        
			return ResponseEntity.ok(userRepository.save(user));
		}
		
		@PutMapping("/{id}/save-place")
		public ResponseEntity<User> addSavedPlaceToUser(@PathVariable Integer id, @RequestBody Integer placeId) {
			Optional<User> oUser = userRepository.findById(id);
	        if (!oUser.isPresent()) {
	            return ResponseEntity.notFound().build();   
	        }
	        User user = oUser.get();
	        
	        Optional<Place> oPlace = placeRepository.findById(placeId);
	        if (!oPlace.isPresent()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        if (!user.getSavedPlaces().contains(oPlace.get())) {
	        	user.getSavedPlaces().add(oPlace.get());
	        }
	        
			return ResponseEntity.ok(userRepository.save(user));
		}
		
		// Methods:
		public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
	    	this.passwordEncoder = passwordEncoder;
	    }
}
