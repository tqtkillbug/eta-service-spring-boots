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

    @Query(value = "SELECT * FROM (SELECT * FROM news where source = \"GenK\" order by created desc limit :limit) as c UNION select * from (SELECT * FROM news where source = \"Kenh14\" order by created desc limit :limit) as k UNION select * from (SELECT * FROM news where source = \"CafeBiz\" order by created desc limit :limit) as g", nativeQuery = true)
    List<New> getListNewLastByLimit(int limit);
}
