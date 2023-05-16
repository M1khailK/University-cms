package ua.foxminded.university.services;

import java.util.List;
import java.util.Optional;

public interface EntityService<T>{

    void save(T entity);

    T getById(Integer id);

    List<T> getAll();

    void deleteById(Integer id);

}
