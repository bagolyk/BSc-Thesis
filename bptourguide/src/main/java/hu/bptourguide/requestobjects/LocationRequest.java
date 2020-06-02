package hu.bptourguide.requestobjects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest implements Serializable{
	
	private Integer postcode;
	private String city;
	private String district;
	private String street;
	private String typeOfStreet;
	private String streetNumber;
	private String stairWay;
	private String floor;
	private String door;
	
	public Boolean isLocation() {
		return this.city != null;
	} 

}

