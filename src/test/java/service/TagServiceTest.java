package service;

import model.Tag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.TagRepository;
import repository.WriterRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {

    static TagRepository tr = null;
    static TagService tagService = null;
    static Tag newTag = null;
    static Tag updateTag = null;
    static String newTagName = "Books";
    static String updatedTagName = "Magazines";

    @BeforeAll
    static void setUp() {

        tr = mock(TagRepository.class);
        tagService = new TagService(tr);

        newTag = new Tag();
        newTag.setTagName(newTagName);

        updateTag = new Tag();
        updateTag.setId(1L);
        updateTag.setTagName(updatedTagName);

        List<Tag> tagList = new ArrayList<>();
        tagList.add(updateTag);

        when(tr.getAll()).thenReturn(tagList);
        when(tr.get(1L)).thenReturn(updateTag);

    }

    @Test
    void tagNameContains() {
        boolean falseResult = tagService.tagNameContains(newTagName);
        boolean trueResult = tagService.tagNameContains(updatedTagName);

        assertFalse(falseResult);
        assertTrue(trueResult);
    }

    @Test
    void containsId() {
        boolean falseResult = tagService.containsId(5L);
        boolean trueResult = tagService.containsId(1L);

        assertFalse(falseResult);
        assertTrue(trueResult);
    }

    @Test
    void getByName() {
        Tag result = tagService.getByName(updatedTagName);

        assertEquals(result,updateTag);
    }

    @Test
    void delete() {
        tagService.delete(updateTag);

        verify(tr).remove(updateTag.getId());
    }
}