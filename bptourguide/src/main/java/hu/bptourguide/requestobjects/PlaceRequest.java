package hu.bptourguide.requestobjects;

import java.util.List;

import hu.bptourguide.entities.Category;
import hu.bptourguide.entities.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceRequest {
	
	private String name;
	
	private String description;
	
	private String openingHours;
	
	private String mainCategory;
	
	private String email;
	
	private String phoneNum;
	
	private String transport;
	
	private String webPage;
	
	private String facebookPage;
	
	private String image;
	
	private Location location;
	
	List<Category> placeCategories;
	
	public Boolean isPlace() {
		return this.name != null && this.mainCategory != null && this.location != null;
	}
}
