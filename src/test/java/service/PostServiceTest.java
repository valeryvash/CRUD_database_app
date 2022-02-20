package service;

import model.Post;
import model.PostStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    static PostRepository pr;
    static PostService postService;
    static Post newPost;
    static Post updPost;
    static Post actStatus;
    static Post deletedStatus;

    @BeforeAll
    static void setUp() {
        pr = mock(PostRepository.class);
        postService = new PostService(pr);

        newPost = new Post();
        newPost.setPostContent("NewPostContent");

        updPost = new Post();
        updPost.setId(2L);
        updPost.setPostContent("UpdPost content");

        actStatus = new Post();
        actStatus.setId(3L);

        deletedStatus = new Post();
        deletedStatus.setId(5L);
        deletedStatus.setPostStatus(PostStatus.DELETED);

        List<Post> postsList = new ArrayList<>();
        postsList.add(updPost);
        postsList.add(actStatus);
        postsList.add(deletedStatus);

        when(pr.getAll()).thenReturn(postsList);
        when(pr.get(2L)).thenReturn(updPost);

        when(pr.containsId(2L)).thenReturn(true);
    }


    @Test
    void containsId() {
        boolean falseResult = postService.containsId(40L);
        boolean trueResult = postService.containsId(2L);

        assertFalse(falseResult);
        assertTrue(trueResult);

        assertThrows(IllegalArgumentException.class,
                () -> postService.containsId(null));
    }

    @Test
    void getById() {
        Post result = postService.getById(2L);

        assertEquals(result, updPost);
    }

    @Test
    void delete() {
        postService.delete(updPost);

        verify(pr).remove(updPost.getId());

        assertThrows(IllegalArgumentException.class,
                () -> postService.delete(new Post()));
    }

    @Test
    void deleteByStatus() {
        postService.deleteByStatus(PostStatus.DELETED);
        verify(pr).deleteByStatus(PostStatus.DELETED);

        postService.deleteByStatus(PostStatus.ACTIVE);
        verify(pr).deleteByStatus(PostStatus.ACTIVE);
    }
}