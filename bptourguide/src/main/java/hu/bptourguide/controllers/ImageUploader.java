package hu.bptourguide.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hu.bptourguide.entities.Event;
import hu.bptourguide.entities.Place;
import hu.bptourguide.repositories.EventRepository;
import hu.bptourguide.repositories.PlaceRepository;

import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin("*")
@RestController
@RequestMapping("/image")
public class ImageUploader {
	
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private PlaceRepository placeRepository;
	
	String folderPath = "D:/ELTE IK/Szakdolgozat/FRONTEND/bptourguide/src/assets/images/data";
	
	/*
	@PostMapping("/{fileName}")
	public ResponseEntity<String> handleFileUpload(@RequestParam("imageFile") MultipartFile file, @PathVariable String fileName) throws IOException {
	    // folderPath here is /sismed/temp/exames
	    String folderPath = "D:/ELTE IK/Szakdolgozat/FRONTEND/bptourguide/src/assets/images/data";
	    String filePath = folderPath + "/" + fileName + ".jpg";

	    // Copies Spring's multipartfile inputStream to /sismed/temp/exames (absolute path)
	    Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	    return ResponseEntity.ok().body("Image upload successful.");
	}
	*/
	
	@PostMapping("event/{eventId}")
	public ResponseEntity<String> eventImageUpload(@RequestParam("imageFile") MultipartFile file, @PathVariable Integer eventId) throws IOException {
		Optional<Event> oEvent = eventRepository.findById(eventId);
        if (!oEvent.isPresent()) {
            return ResponseEntity.notFound().build();   
        }
        Event event = oEvent.get();
        String fileName = "event" + event.getId() + ".jpg";
	    String filePath = folderPath + "/" + fileName;
	    event.setImage("assets/images/data/" + fileName);
	    eventRepository.save(event);
	    
	    // Copies Spring's multipartfile inputStream to absolute path
	    Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	    return ResponseEntity.ok().body("Image upload successful.");
	}
	
	@PostMapping("place/{placeId}")
	public ResponseEntity<String> placeImageUpload(@RequestParam("imageFile") MultipartFile file, @PathVariable Integer placeId) throws IOException {
		Optional<Place> oPlace = placeRepository.findById(placeId);
        if (!oPlace.isPresent()) {
            return ResponseEntity.notFound().build();   
        }
        Place place = oPlace.get();
        String fileName = "place" + place.getId() + ".jpg";
	    String filePath = folderPath + "/" + fileName;
	    place.setImage("assets/images/data/" + fileName);
	    placeRepository.save(place);
	    
	    // Copies Spring's multipartfile inputStream to absolute path
	    Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
	    return ResponseEntity.ok().body("Image upload successful.");
	}
}
