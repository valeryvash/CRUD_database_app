package service;

import model.Post;
import model.PostStatus;
import repository.PostRepository;

import java.util.List;

import static util.EntitiesIdCheck.*;

public class PostService {
    private PostRepository postRepository;

    private PostService() {
    }

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void add(Post entity) {
        throwIfIdIsPositive(entity);
        postRepository.add(entity);
    }

    public Post get(Long aLong) {
        throwIfIdIsSmallerThanOne(aLong);
        return postRepository.get(aLong);
    }

    public void update(Post entity) {
        throwIfIdIsSmallerThanOne(entity);
        postRepository.update(entity);
    }

    public void remove(Long aLong) {
        throwIfIdIsSmallerThanOne(aLong);
        postRepository.remove(aLong);
    }

    public List<Post> getAll() {
        return postRepository.getAll();
    }

    public boolean containsId(Long id) {
        throwIfIdIsSmallerThanOne(id);
        return postRepository.containsId(id);
    }

    public Post getById(Long id) {
        throwIfIdIsSmallerThanOne(id);
        return get(id);
    }

    public void delete(Post p) {
        throwIfIdIsSmallerThanOne(p);
        remove(p.getId());
    }

    public void deleteByStatus(PostStatus ps) {
        postRepository.deleteByStatus(ps);
    }
}
