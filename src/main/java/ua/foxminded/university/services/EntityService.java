package ua.foxminded.university.services;

import java.util.List;

public interface EntityService<T>{

    void save(T entity);

    T getById(int id);

    List<T> getAll();
}
