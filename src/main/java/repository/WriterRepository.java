package repository;

import model.Writer;

import java.util.List;

public interface WriterRepository extends GenericRepository<Long, Writer> {

    @Override
    void add(Writer entity);

    @Override
    Writer get(Long id);

    @Override
    void update(Writer entity);

    @Override
    void remove(Long id);

    @Override
    List<Writer> getAll();
}
