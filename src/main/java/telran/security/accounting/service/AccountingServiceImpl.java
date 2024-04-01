package telran.security.accounting.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	final PasswordEncoder passwordEncoder;

	FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);

	@Override
	public AccountDto addAccount(AccountDto accountDto) {
		String email = accountDto.email();
		Account acc;
		AccountDto encodedAccount = getEncoded(accountDto);
		try {
			acc = mongoTemplate.insert(Account.of(encodedAccount));
		} catch (DuplicateKeyException e) {
			log.error("----> Account {}, already exists", email);
			throw new AccountStateException(email);
		}
		log.debug("----> Account {} has been added", email);
		return acc.build();
	}

	private AccountDto getEncoded(AccountDto accountDto) {
		return new AccountDto(accountDto.email(), passwordEncoder.encode(accountDto.password()), accountDto.roles());
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

	@Override
	public void updatePassword(String email, String newPassword)
			throws AccountNotFoundException, IllegalArgumentException {
		String currentUserName = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.debug("----> get authentication from SecurityContextHolder: {} ", authentication);
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			currentUserName = authentication.getName();
			log.debug("----> currentUserName: {} ", currentUserName);
		}
		
		log.debug("----> email in request: {} ", email);
		if (!currentUserName.equals(email) ||  currentUserName == null ) {
			log.error("----> No permission for this user");
			throw new IllegalArgumentException("No permission");
		}

		Query query = new Query(Criteria.where("email").is(email));
		Update update = new Update();
		update.set("password", passwordEncoder.encode(newPassword));
		Account res = mongoTemplate.findAndModify(query, update, options, Account.class);
		if (res == null ) {
			log.error("----> AccountNotFoundException {}, email");
			throw new AccountNotFoundException(email);
		}
		log.debug("----> newPassword {} has been updated for email: {}", newPassword, email);
	}

}
