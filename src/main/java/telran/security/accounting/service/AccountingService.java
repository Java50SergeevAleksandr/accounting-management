package telran.security.accounting.service;

import telran.probes.exceptions.AccountNotFoundException;
import telran.security.accounting.dto.AccountDto;

public interface AccountingService {
	AccountDto addAccount(AccountDto account);

	AccountDto removeAccount(String email);

	void updatePassword(String email, String newPassword) throws AccountNotFoundException, IllegalArgumentException;
}
