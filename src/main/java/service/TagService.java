package service;

import model.Tag;
import repository.TagRepository;

import java.util.List;

import static util.EntitiesIdCheck.*;

public class TagService {
    private TagRepository tagRepository;

    private TagService() {
    }

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag add(Tag entity) {
        throwIfIdIsPositive(entity);
        return tagRepository.add(entity);
    }

    public Tag getById(Long aLong) {
        throwIfIdIsSmallerThanOne(aLong);
        return tagRepository.get(aLong);
    }

    public Tag update(Tag entity) {
        throwIfIdIsSmallerThanOne(entity);
        return tagRepository.update(entity);
    }

    public void remove(Long aLong) {
        throwIfIdIsSmallerThanOne(aLong);
        tagRepository.remove(aLong);
    }

    public List<Tag> getAll() {
        return tagRepository.getAll();
    }

    public boolean tagNameContains(String s) {
        throwIfObjectIsNull(s);
        return tagRepository.nameContains(s);
    }

    public boolean containsId(Long id) {
        throwIfIdIsSmallerThanOne(id);
        return tagRepository.containsId(id);
    }

    public Tag getByName(String s) {
        throwIfObjectIsNull(s);
        return tagRepository.getByName(s);
    }

    public void delete(Tag t) {
        throwIfIdIsSmallerThanOne(t);
        remove(t.getId());
    }
}
