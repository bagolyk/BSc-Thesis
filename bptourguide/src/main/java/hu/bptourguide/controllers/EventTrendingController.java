package hu.bptourguide.controllers;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.bptourguide.entities.Event;
import hu.bptourguide.entities.EventTrending;
import hu.bptourguide.repositories.EventRepository;
import hu.bptourguide.requestobjects.EventTrendingRequestForSending;
import hu.bptourguide.requestobjects.UserRequest;
import hu.bptourguide.repositories.EventTrendingRepository;

@CrossOrigin("*")
@RestController
@RequestMapping("/event-trending")
public class EventTrendingController {

	@Autowired
	private EventTrendingRepository eventTrendingRepository;

	@Autowired
	private EventRepository eventRepository;

	//private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	// GET mapping:
	
	@GetMapping("")
	public ResponseEntity<List<EventTrendingRequestForSending>> getTrendingEvents() {
		//System.out.println(auth.getDetails());
		System.out.println("sajt");
		Iterable<EventTrending> events = eventTrendingRepository.findAll();
		Map<Integer, Integer> trending = new HashMap<Integer, Integer>();
		for (EventTrending event : events) {
			Integer ev = event.getTrendingEvent().getId();
			if (inTimeWindow(event.getTimestamp(), 48)) {
				if (trending.containsKey(ev)) {
					int v = trending.get(ev);
					v++;
					trending.replace(ev, v);
				} else {
					trending.put(ev, 1);
				}
			}
		}
		
		List<EventTrendingRequestForSending> sortedTrending = new ArrayList<>();
		trending.forEach((key,value) -> sortedTrending.add(new EventTrendingRequestForSending(value, eventRepository.findById(key).get())));
		Collections.sort(sortedTrending, Collections.reverseOrder());
		
		return ResponseEntity.status(HttpStatus.OK).body(sortedTrending);
	}
	
	@GetMapping("/getall")
	public ResponseEntity<Iterable<EventTrending>> getAll(Authentication auth) {
		System.out.println(auth.getDetails());
		Iterable<EventTrending> events = eventTrendingRepository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(events);
	}
	
	// POST mappings:

	@PostMapping("/new")
	public ResponseEntity<EventTrending> createEventTrending(@RequestBody Integer trendingEventId) {
		Optional<Event> optEvent = eventRepository.findById(trendingEventId);
		if (!optEvent.isPresent()) {
			System.out.println("ROSSZ ADAT ERKEZETT!!!4!!");
			return ResponseEntity.badRequest().build();
		}

		EventTrending newEvent = new EventTrending(0, new Timestamp(System.currentTimeMillis()), optEvent.get());
		return ResponseEntity.ok(eventTrendingRepository.save(newEvent));
	}
	
	// DELETE mappings:

	@SuppressWarnings("rawtypes")
	@PostMapping("/delete/")
	public ResponseEntity deleteOldLogs() {
		Iterable<EventTrending> eventTrendings = eventTrendingRepository.findAll();
		for (EventTrending et : eventTrendings) {
			if (!inTimeWindow(et.getTimestamp(), 168)) {
				eventTrendingRepository.delete(et);
			}
		}
		return ResponseEntity.ok().build();
	}
	
	// Methods

	public boolean inTimeWindow(Timestamp arg, int hour) {
		// 48 hour time window in case of trending events, 1 hour = 3.600.000
		// milliseconds
		long timewindow = hour * 3600000;
		Timestamp tstamp = new Timestamp(System.currentTimeMillis() - timewindow);
		// If the argument Timestamp is earlier than x hour, this will return false.
		return !(arg.before(tstamp));
	}
	
}
