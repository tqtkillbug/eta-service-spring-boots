package net.etaservice.appmanager.repository;

import net.etaservice.appmanager.model.RequestApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequetsAppRepository extends JpaRepository<RequestApp,Long> {
}
