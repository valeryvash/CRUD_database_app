package repository;

import java.util.List;

public interface GenericRepository<ID,T> {

    T add(T entity);

    T get(ID id);

    T update(T entity);

    void remove(ID id);

     List<T> getAll();

}
