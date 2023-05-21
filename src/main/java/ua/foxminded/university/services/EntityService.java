package ua.foxminded.university.services;

import java.util.List;

public interface EntityService<T>{

    void save(T entity);

    T getById(Integer id);

    List<T> getAll();

    void deleteById(Integer id);

}
