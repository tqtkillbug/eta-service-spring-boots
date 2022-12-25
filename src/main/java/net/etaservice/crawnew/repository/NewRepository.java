package net.etaservice.crawnew.repository;

import net.etaservice.crawnew.model.New;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface NewRepository extends JpaRepository<New, Long> {

    @Query(value = "SELECT * FROM news u WHERE u.created like :created and u.source = :source", nativeQuery = true)
    List<New> getListNewBySourceAndDate(@Param("source") String source, @Param("created") String created);
}
