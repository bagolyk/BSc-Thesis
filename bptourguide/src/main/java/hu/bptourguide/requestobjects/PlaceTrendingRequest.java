package hu.bptourguide.requestobjects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceTrendingRequest implements Serializable, Comparable<PlaceTrendingRequest> {
	
	private Integer eventId;
	private Integer count;
	
	@Override
	public int compareTo(PlaceTrendingRequest arg) {
		return this.count - arg.count;
	}
}
