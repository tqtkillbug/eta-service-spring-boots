package net.etaservice.myportfolio.repository;

import net.etaservice.myportfolio.model.SourceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceTransactionRepository extends JpaRepository<SourceTransaction, Long> {
}
