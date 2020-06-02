package hu.bptourguide.requestobjects;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import hu.bptourguide.entities.Category;
import hu.bptourguide.entities.Place;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestWithPlace {
	
	private String name;
	
	private String description;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm", timezone = "GMT+1")
	private Timestamp startDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm", timezone = "GMT+1")
	private Timestamp endDate;
	
	private String mainCategory;
	
	private String webPage;
	
	private String facebookLink;
	
	private String image;

	private Place place;
	
	List<Category> eventCategories;
	
	public Boolean isEvent() {
		return this.name != null && this.mainCategory != null && this.startDate != null && this.endDate != null;
	}
}
