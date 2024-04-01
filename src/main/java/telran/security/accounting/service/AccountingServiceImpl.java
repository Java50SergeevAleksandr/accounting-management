package telran.security.accounting.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.exceptions.AccountNotFoundException;
import telran.probes.exceptions.AccountStateException;

import telran.security.accounting.dto.AccountDto;
import telran.security.accounting.model.Account;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {
	final MongoTemplate mongoTemplate;

	@Override
	public AccountDto addAccount(AccountDto accountDto) {
		String email = accountDto.email();
		Account acc;
		try {
			acc = mongoTemplate.insert(Account.of(accountDto));
		} catch (DuplicateKeyException e) {
			log.error("----> Account {}, already exists", email);
			throw new AccountStateException(email);
		}
		log.debug("----> Account {} has been added", email);
		return acc.build();
	}

	@Override
	public AccountDto removeAccount(String email) {
		Account acc = mongoTemplate.findAndRemove(new Query(Criteria.where("email").is(email)), Account.class);

		if (acc == null) {
			log.error("----> Account {} not found", email);
			throw new AccountNotFoundException(email);
		}
		log.debug("----> Account {} has been removed", email);
		return acc.build();
	}

}
