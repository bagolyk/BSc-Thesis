package hu.bptourguide.entities;

import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event")
public class Event implements Serializable, Comparable<Event> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EVENT_ID")
	@NotNull
	private Integer id;
	
	@Column(name = "NAME")
	@NotNull
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "START_DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm", timezone = "GMT+1")
	private Timestamp startDate;
	
	@Column(name = "END_DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd. HH:mm", timezone = "GMT+1")
	private Timestamp endDate;
	
	@Column(name = "MAIN_CATEGORY")
	private String mainCategory;
	
	@Column(name = "WEB_PAGE")
	private String webPage;
	
	@Column(name = "FACEBOOK_LINK")
	private String facebookLink;
	
	@Column(name = "IMAGE")
	private String image;
	
	@ManyToOne
    @JoinColumn(name="PLACE_ID")
	private Place place;
	
	@JsonIgnore
	@OneToMany(mappedBy="trendingEvent")
    private List<EventTrending> trendingsBySearches;
	
	@ManyToMany
	@JoinTable(
			name="event_has_category",
			joinColumns = @JoinColumn(name = "EVENT_ID"),
			inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
	List<Category> eventCategories;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "savedEvents")
	List<User> users;
	
	@JsonIgnore
	public Boolean isEvent() {
		return this.name != null && this.mainCategory != null && this.startDate != null && this.endDate != null;
	}
	
	@JsonIgnore
	public void addCategory(Category category) {
		eventCategories.add(category);
	}

	@JsonIgnore
	@Override
	public int compareTo(Event arg) {
		if (this.getStartDate() == null || arg.getStartDate() == null) {
			return 0;
		}
		return this.getStartDate().compareTo(arg.getStartDate());
	}
	
}
