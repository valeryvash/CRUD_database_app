package repository;

import model.Writer;

import java.util.List;

public interface WriterRepository extends GenericRepository<Long, Writer> {

    @Override
    Writer add(Writer entity);

    @Override
    Writer get(Long id);

    @Override
    Writer update(Writer entity);

    @Override
    void remove(Long id);

    @Override
    List<Writer> getAll();

    boolean nameContains(String name);

    boolean containsId(Long id);

    Writer getByName(String name);

}
