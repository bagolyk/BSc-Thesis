package hu.bptourguide.requestobjects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTrendingRequest implements Serializable, Comparable<EventTrendingRequest> {

	private Integer eventId;
	private Integer count;
	
	@Override
	public int compareTo(EventTrendingRequest arg) {
		return this.count - arg.count;
	}
}
