package telran.security.accounting.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.security.accounting.dto.AccountDto;
import telran.security.accounting.dto.PasswordUpdateDataDto;
import telran.security.accounting.service.AccountingService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("accounts")
public class AccountingController {
	final AccountingService accountingService;

	@PostMapping
	AccountDto addAccount(@RequestBody @Valid AccountDto accountDto) {
		log.debug("----> addAccount: received accountDto: {}", accountDto);
		return accountingService.addAccount(accountDto);
	}

	@DeleteMapping("{email}")
	AccountDto deleteAccount(@PathVariable String email) {
		log.debug("----> deleteAccount: received email: {}", email);
		return accountingService.removeAccount(email);
	}

	@PutMapping("/update/password")
	PasswordUpdateDataDto updatePassword(@RequestBody @Valid PasswordUpdateDataDto passwordUpdateDataDto) {
		accountingService.updatePassword(passwordUpdateDataDto.email(), passwordUpdateDataDto.password());
		return passwordUpdateDataDto;

	}
}
