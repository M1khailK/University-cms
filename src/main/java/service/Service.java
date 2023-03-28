package service;

import java.util.List;
import java.util.Optional;

public interface Service<T>{

    void save(T entity);

    Optional<T> getById(Integer id);

    List<T> getAll();

    void deleteById(Integer id);

}
