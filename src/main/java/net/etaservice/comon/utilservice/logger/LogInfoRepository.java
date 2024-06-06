package net.etaservice.comon.utilservice.logger;

import lombok.Data;
import net.etaservice.comon.utilservice.logger.model.LogInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LogInfoRepository extends JpaRepository<LogInfo,Long> {
  List<LogInfo> findAllByApp(String app);
  List<LogInfo> findAllByAppAndProfileAndCreateDateAfter(String app,String profile,Date date);

}
