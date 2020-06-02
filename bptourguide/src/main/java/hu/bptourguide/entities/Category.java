package hu.bptourguide.entities;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CATEGORY_ID")
	@NotNull
	private Integer id;
	
	@Column(name = "NAME")
	@NotNull
	private String name;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "placeCategories")
	List<Place> places;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "eventCategories")
	List<Event> events;
	
	@JsonIgnore
	public Boolean isCategory() {
		return this.name != null;
	}
	
}
