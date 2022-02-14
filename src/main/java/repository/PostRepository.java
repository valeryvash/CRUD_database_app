package repository;

import model.Post;

import java.util.List;

public interface PostRepository extends GenericRepository<Long, Post> {

    @Override
    void add(Post entity);

    @Override
    Post get(Long aLong);

    @Override
    void update(Post entity);

    @Override
    void remove(Long aLong);

    @Override
    List<Post> getAll();
}
