package net.etaservice.appmanager.repository;

import net.etaservice.appmanager.model.RequestApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RequetsAppRepository extends JpaRepository<RequestApp,Long> {
      @Query("SELECT COUNT(e) FROM RequestApp e WHERE DATE(e.requestDate) = :date")
      Long countByRequestDate(@Param("date") Date date);

}
