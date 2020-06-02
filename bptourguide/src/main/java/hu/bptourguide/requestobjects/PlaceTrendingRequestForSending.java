package hu.bptourguide.requestobjects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import hu.bptourguide.entities.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceTrendingRequestForSending implements Serializable, Comparable<PlaceTrendingRequestForSending> {
	
	@JsonProperty("count")
	private Integer count;
	
	@JsonProperty("place")
	private Place place;

	@Override
	public int compareTo(PlaceTrendingRequestForSending arg0) {
		return this.count - arg0.count;
	}
}
