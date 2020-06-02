package hu.bptourguide.requestobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	
	private String username;
	
	private String email;
	
	private String password;	
	
	@JsonIgnore
	public Boolean isUser() {
		return this.username != null && this.email != null && this.password != null; 
	}
	
	@JsonIgnore
	public Boolean isComplete() {
		return this.username != null && this.password != null; 
	}
}
