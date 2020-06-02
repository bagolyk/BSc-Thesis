package hu.bptourguide.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place")
public class Place implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PLACE_ID")
	@NotNull
	private Integer id;
	
	@Column(name = "NAME")
	@NotNull
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "OPENING_HOURS")
	private String openingHours;
	
	@Column(name = "MAIN_CATEGORY")
	@NotNull
	private String mainCategory;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "PHONE_NUM")
	private String phoneNum;
	
	@Column(name = "TRANSPORT")
	private String transport;
	
	@Column(name = "WEB_PAGE")
	private String webPage;
	
	@Column(name = "FACEBOOK_PAGE")
	private String facebookPage;
	
	@Column(name = "IMAGE")
	private String image;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID")
	@NotNull
	private Location location;
	
	@JsonIgnore
	@OneToMany(mappedBy="place", cascade = CascadeType.ALL)
    private List<Event> events;
	
	@JsonIgnore
	@OneToMany(mappedBy="trendingPlace")
    private List<PlaceTrending> trendings;
	
	@ManyToMany
	@JoinTable(
			name = "place_has_category",
			joinColumns = @JoinColumn(name = "PLACE_ID"),
			inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID"))
	List<Category> placeCategories;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "savedPlaces")
	List<User> users;
	
	@JsonIgnore
	public void addCategory(Category category) {
		placeCategories.add(category);
	}
	
	@JsonIgnore
	public Boolean isPlace() {
		return this.name != null && this.mainCategory != null && this.location != null;
	}

}
