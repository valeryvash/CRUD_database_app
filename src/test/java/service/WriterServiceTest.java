package service;

import model.Writer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.WriterRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WriterServiceTest {

    static WriterRepository wr;
    static WriterService writerService;
    static Writer newWriter ;
    static Writer updateWriter;

    @BeforeAll
    static void setUp() {
        wr = mock(WriterRepository.class);
        writerService = new WriterService(wr);
        newWriter = new Writer();
        newWriter.setWriterName("Ashley");

        updateWriter = new Writer();
        updateWriter.setWriterName("Britney");
        updateWriter.setId(1L);

        when(wr.get(1L)).thenReturn(updateWriter);

        when(wr.nameContains("Britney")).thenReturn(true);
        when(wr.containsId(1L)).thenReturn(true);

        when(wr.getByName("Britney")).thenReturn(updateWriter);
    }


    @Test
    void writerNameContains() {
        boolean falseCall = writerService.writerNameContains("Ashley");
        boolean trueCall = writerService.writerNameContains("Britney");

        assertFalse(falseCall);
        assertTrue(trueCall);

        assertThrows(IllegalArgumentException.class,
                () -> writerService.writerNameContains(null));
    }

    @Test
    void containsId() {
        boolean falseResult = writerService.containsId(5L);
        boolean trueResult = writerService.containsId(1L);

        assertFalse(falseResult);
        assertTrue(trueResult);

        assertThrows(IllegalArgumentException.class,
                () -> writerService.writerNameContains(null));
    }

    @Test
    void getByIdTest() {
        Writer result = writerService.get(1L);

        assertEquals(updateWriter, result);

        assertThrows(IllegalArgumentException.class, () -> writerService.get(-1L));
        assertThrows(IllegalArgumentException.class, () -> writerService.get(0L));
    }

    @Test
    void getByName() {
        Writer result = writerService.getByName("Britney");

        assertEquals(updateWriter, result);

        assertThrows(IllegalArgumentException.class,
                () -> writerService.writerNameContains(null));
    }

    @Test
    void delete() {
        writerService.delete(updateWriter);

        verify(wr).remove(updateWriter.getId());
    }

}