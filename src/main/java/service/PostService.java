package service;

import model.Post;
import model.PostStatus;
import model.Tag;
import model.Writer;
import repository.PostRepository;
import repository.TagRepository;
import repository.WriterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static util.EntitiesIdCheck.*;

public class PostService {
    private PostRepository postRepository;
    private WriterRepository writerRepository;
    private TagRepository tagRepository;

    private PostService() {
    }

    public PostService(PostRepository postRepository, WriterRepository writerRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.writerRepository = writerRepository;
        this.tagRepository = tagRepository;
    }

    private List<Tag> tagsValidation(Post p) {
        List<Tag> tags = new ArrayList<>(p.getPostTags());
        if (!tags.isEmpty()) {
            List<Tag> validatedTags = tags.stream().map(t ->
                    {
                        if (t.getId() < 1L) {
                            String tagName = t.getTagName();
                            if (tagRepository.nameContains(tagName)) {
                                return tagRepository.getByName(tagName);
                            } else {
                                return tagRepository.add(t);
                            }
                        } else {
                            return t;
                        }
                    }
            ).collect(Collectors.toList());
            return validatedTags;
        }
        return tags;
    }

    public Post add(Post entity) {
        throwIfIdIsPositive(entity);
        List<Tag> tags = tagsValidation(entity);
        entity.setPostTags(tags);
        return postRepository.add(entity);
    }

    public Post get(Long aLong) {
        throwIfIdIsSmallerThanOne(aLong);
        long writerId = postRepository.getWriterId(aLong);
        Writer w = writerRepository.get(writerId);
        List<Post> writerPosts = postRepository.getPostsForWriter(writerId);
        writerPosts.forEach(p -> p.setWriter(w));
        return writerPosts
                .stream()
                .filter(post -> Objects.equals(post.getId(), aLong))
                .findAny()
                .orElse(new Post());
    }

    public Post update(Post entity) {
        throwIfIdIsSmallerThanOne(entity);
        List<Tag> tags = tagsValidation(entity);
        entity.setPostTags(tags);
        return postRepository.update(entity);
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
        return this.get(id);
    }

    public void delete(Post p) {
        throwIfIdIsSmallerThanOne(p);
        remove(p.getId());
    }

    public void deleteByStatus(PostStatus ps) {
        postRepository.deleteByStatus(ps);
    }
}
