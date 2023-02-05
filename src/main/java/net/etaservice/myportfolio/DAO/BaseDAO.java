package net.etaservice.myportfolio.DAO;

import java.util.List;

public interface BaseDAO<T> {

    T getById(Long id);

    T save(T t);

    List<T> getAll();

}
