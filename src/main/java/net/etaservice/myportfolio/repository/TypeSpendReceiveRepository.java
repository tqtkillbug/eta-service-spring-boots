package net.etaservice.myportfolio.repository;

import net.etaservice.myportfolio.model.TypeSpendReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeSpendReceiveRepository extends JpaRepository<TypeSpendReceive, Long> {
}
