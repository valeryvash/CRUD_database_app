package service;

import model.Post;
import model.PostStatus;
import repository.PostRepository;

import java.util.List;

public class PostService {
    private PostRepository postRepository;

    private PostService() {
    }

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void add(Post entity) {
        postRepository.add(entity);
    }

    public Post get(Long aLong) {
        return postRepository.get(aLong);
    }

    public void update(Post entity) {
        postRepository.update(entity);
    }

    public void remove(Long aLong) {
        postRepository.remove(aLong);
    }

    public List<Post> getAll() {
        return postRepository.getAll();
    }

    public boolean containsId(Long id) {
        return getAll()
                .stream()
                .anyMatch(post -> post.getId().equals(id));
    }

    public Post getById(Long id) {
        return get(id);
    }

    public void delete(Post p) {
        remove(p.getId());
    }

    public void deleteByStatus(PostStatus ps) {
        getAll().forEach(post ->
                {
                    if (post.getPostStatus().equals(ps)) {
                        remove(post.getId());
                    }
                });
    }
}
