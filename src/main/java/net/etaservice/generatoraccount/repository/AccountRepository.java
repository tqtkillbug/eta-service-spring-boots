package net.etaservice.generatoraccount.repository;

import net.etaservice.generatoraccount.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
