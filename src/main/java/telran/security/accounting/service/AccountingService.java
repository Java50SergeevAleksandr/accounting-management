package telran.security.accounting.service;

import telran.security.accounting.dto.AccountDto;

public interface AccountingService {
	AccountDto addAccount(AccountDto account);

	AccountDto removeAccount(String email);
}
