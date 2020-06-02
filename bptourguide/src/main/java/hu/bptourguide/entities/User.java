package hu.bptourguide.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "user")
public class User implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	@NotNull
	private Integer id;
	
	@Column(name = "USERNAME", unique = true)
	@NotNull
	private String username;
	
	@Column(name = "EMAIL")
	@NotNull
	private String email;
	
	@Column(name = "PASSWORD")
	@NotNull
	private String password;
	
	@Column(name = "ROLE")
	@NotNull
	private String role;
	
	@ManyToMany
	@JoinTable(
			name = "user_saved_places",
			joinColumns = @JoinColumn(name = "USER_ID"),
			inverseJoinColumns = @JoinColumn(name = "PLACE_ID"))
	List<Place> savedPlaces;
	
	@ManyToMany
	@JoinTable(
			name = "user_saved_events",
			joinColumns = @JoinColumn(name = "USER_ID"),
			inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
	List<Event> savedEvents;
	
	@JsonIgnore
	public Boolean isUser() {
		return this.username != null && this.email != null && this.password != null; 
	}
}
