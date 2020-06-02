package hu.bptourguide.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.bptourguide.entities.Place;
import hu.bptourguide.entities.PlaceTrending;
import hu.bptourguide.repositories.PlaceRepository;
import hu.bptourguide.repositories.PlaceTrendingRepository;
import hu.bptourguide.requestobjects.PlaceTrendingRequestForSending;

@CrossOrigin("*")
@RestController
@RequestMapping("/place-trending")
public class PlaceTrendingController {

	@Autowired
	private PlaceTrendingRepository placeTrendingRepository;

	@Autowired
	private PlaceRepository placeRepository;

	// GET mappings:

	@GetMapping("/getall")
	public ResponseEntity<Iterable<PlaceTrending>> getAll() {
		Iterable<PlaceTrending> places = placeTrendingRepository.findAll();
		return ResponseEntity.status(HttpStatus.OK).body(places);
	}

	@GetMapping("")
	public ResponseEntity<List<PlaceTrendingRequestForSending>> getTrendingPlaces() {
		Iterable<PlaceTrending> places = placeTrendingRepository.findAll();
		Map<Integer, Integer> trending = new HashMap<Integer, Integer>();
		for (PlaceTrending place : places) {
			Integer pl = place.getTrendingPlace().getId();
			if (inTimeWindow(place.getTimestamp(), 48)) {
				if (trending.containsKey(pl)) {
					int v = trending.get(pl);
					v++;
					trending.replace(pl, v);
				} else {
					trending.put(pl, 1);
				}
			}
		}

		List<PlaceTrendingRequestForSending> sortedTrending = new ArrayList<>();
		trending.forEach((key, value) -> sortedTrending
				.add(new PlaceTrendingRequestForSending(value, placeRepository.findById(key).get())));
		Collections.sort(sortedTrending);
		return ResponseEntity.status(HttpStatus.OK).body(sortedTrending);
	}

	// POST mappings:

	@PostMapping("/new")
	public ResponseEntity<PlaceTrending> createPlaceTrending(@RequestBody Integer trendingPlaceId) {
		Optional<Place> optPlace = placeRepository.findById(trendingPlaceId);
		if (!optPlace.isPresent()) {
			System.out.println("ROSSZ ADAT ERKEZETT!!!4!!");
			return ResponseEntity.badRequest().build();
		}

		PlaceTrending newPlace = new PlaceTrending(0, new Timestamp(System.currentTimeMillis()), optPlace.get());
		return ResponseEntity.ok(placeTrendingRepository.save(newPlace));
	}

	// DELETE mappings:

	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete")
	public ResponseEntity deleteOldLogs() {
		Iterable<PlaceTrending> placeTrendings = placeTrendingRepository.findAll();
		for (PlaceTrending pl : placeTrendings) {
			if (!inTimeWindow(pl.getTimestamp(), 168)) {
				placeTrendingRepository.delete(pl);
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
