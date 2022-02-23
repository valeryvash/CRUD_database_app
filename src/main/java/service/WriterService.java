package service;

import model.Post;
import model.Tag;
import model.Writer;
import repository.PostRepository;
import repository.TagRepository;
import repository.WriterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.EntitiesIdCheck.*;

public class WriterService {
    private WriterRepository writerRepository;
    private PostRepository postRepository;
    private TagRepository tagRepository;

    private WriterService() {
    }

    public WriterService(WriterRepository writerRepository, PostRepository postRepository, TagRepository tagRepository) {
        this.writerRepository = writerRepository;
        this.postRepository = postRepository;
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

    public Writer add(Writer entity) {
        throwIfIdIsPositive(entity);
        List<Post> posts = entity.getWriterPosts();
        if (!posts.isEmpty()) {
            for (Post post : posts) {
                post.setPostTags(tagsValidation(post));
            }
        }
        return writerRepository.add(entity);
    }

    public Writer get(Long id) {
        throwIfIdIsSmallerThanOne(id);
        if (writerRepository.containsId(id)) {
            Writer writerToBeReturned = writerRepository.get(id);
            List<Post> writerPosts = postRepository.getPostsForWriter(id);
            writerPosts.forEach(p -> p.setWriter(writerToBeReturned));
            writerToBeReturned.setWriterPosts(writerPosts);
            return writerToBeReturned;
        } else {
            throw new IllegalArgumentException("Repo has no writer with such id");
        }
    }

    public Writer update(Writer entity) {
        throwIfIdIsSmallerThanOne(entity);
        List<Post> posts = entity.getWriterPosts();
        if (!posts.isEmpty()) {
            for (Post post : posts) {
                post.setPostTags(tagsValidation(post));
            }
        }
        return writerRepository.update(entity);
    }

    public void remove(Long id) {
        throwIfIdIsSmallerThanOne(id);
        writerRepository.remove(id);
    }

    public List<Writer> getAll() {
        return writerRepository.getAll();
    }

    public boolean writerNameContains(String s) {
        throwIfObjectIsNull(s);
        return writerRepository.nameContains(s);
    }

    public boolean containsId(Long id) {
        throwIfIdIsSmallerThanOne(id);
        return writerRepository.containsId(id);
    }

    public Writer getById(Long id) {
        throwIfObjectIsNull(id);
        return this.get(id);
    }

    public Writer getByName(String s) {
        throwIfObjectIsNull(s);
        Writer toBeReturned = writerRepository.getByName(s);
        long writerId = toBeReturned.getId();
        if (writerId != 0L) {
            List<Post> writerPosts = postRepository.getPostsForWriter(writerId);
            toBeReturned.setWriterPosts(writerPosts);
            if (!writerPosts.isEmpty()) {
                writerPosts.forEach(p -> p.setWriter(toBeReturned));
            }
            return toBeReturned;
        } else {
            throw new IllegalArgumentException("Repo has no writer with such name");
        }
    }

    public void delete(Writer w) {
        remove(w.getId());
    }
}
