package hu.bptourguide.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "event_trending")
public class EventTrending implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@NotNull
	private Integer id;
	
	@Column(name = "TIME_STAMP")
	private Timestamp timestamp;
	
	@ManyToOne
    @JoinColumn(name="EVENT_ID")
	@NotNull
	private Event trendingEvent;
	
	@JsonIgnore
	public Boolean isEventTrendingBySearches() {
		return this.timestamp != null && this.trendingEvent != null;
	}
	
}
