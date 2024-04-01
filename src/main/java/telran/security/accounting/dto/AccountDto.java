package telran.security.accounting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AccountDto(@NotEmpty(message = "missing email") @Email(message = "wrong email format") String email,
		@NotEmpty(message = "missing password") @Size(min = 8) String password,
		@NotEmpty(message = "missing roles") @Size(min = 1) String[] roles) {

}
