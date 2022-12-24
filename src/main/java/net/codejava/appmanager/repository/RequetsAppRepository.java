package net.codejava.appmanager.repository;

import net.codejava.appmanager.model.RequestApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequetsAppRepository extends JpaRepository<RequestApp,Long> {
}
