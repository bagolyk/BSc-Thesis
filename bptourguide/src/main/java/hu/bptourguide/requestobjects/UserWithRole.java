package hu.bptourguide.requestobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithRole {
	
	private int id;
	
	private String username;
	
	private String email;
	
	//private String password;
	
	private String role;
}
