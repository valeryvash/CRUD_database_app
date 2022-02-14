package service;

import model.Tag;
import repository.TagRepository;

import java.util.List;
import java.util.Optional;

public class TagService {
    private TagRepository tagRepository;

    private TagService() {
    }

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void add(Tag entity) {
        tagRepository.add(entity);
    }

    public Tag getById(Long aLong) {
        return tagRepository.get(aLong);
    }

    public void update(Tag entity) {
        tagRepository.update(entity);
    }

    public void remove(Long aLong) {
        tagRepository.remove(aLong);
    }

    public List<Tag> getAll() {
        return tagRepository.getAll();
    }

    public boolean tagNameContains(String s) {
        return tagRepository
                .getAll()
                .stream()
                .anyMatch(tag -> tag.getTagName().equals(s));
    }

    public boolean containsId(Long id) {
        return tagRepository
                .getAll()
                .stream()
                .anyMatch(tag -> tag.getId().equals(id));
    }

    public Tag getByName(String s) {
        Tag toBeReturned = new Tag();
        Optional<Tag> optionalTag = getAll().stream().filter(tag -> tag.getTagName().equals(s)).findAny();

        return optionalTag.orElseThrow();
    }

    public void delete(Tag t) {
        remove(t.getId());
    }
}
