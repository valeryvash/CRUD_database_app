package repository;

import java.util.List;

public interface GenericRepository<ID,T> {

    public void add(T entity);

    public T get(ID id);

    public void update(T entity);

    public void remove(ID id);

    public List<T> getAll();

}
