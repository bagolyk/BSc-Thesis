package hu.bptourguide.requestobjects;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import hu.bptourguide.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTrendingRequestForSending implements Serializable, Comparable<EventTrendingRequestForSending> {

	@JsonProperty("count")
	private Integer count;
	
	@JsonProperty("event")
	private Event event;

	@Override
	public int compareTo(EventTrendingRequestForSending arg0) {
		return this.count - arg0.count;
	}
}