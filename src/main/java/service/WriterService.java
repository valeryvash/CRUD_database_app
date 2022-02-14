package service;

import model.Writer;
import repository.WriterRepository;

import java.util.List;

public class WriterService {
    private WriterRepository writerRepository;

    private WriterService() {
    }

    public WriterService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    public void add(Writer entity) {
        writerRepository.add(entity);
    }

    public Writer get(Long id) {
        return writerRepository.get(id);
    }

    public void update(Writer entity) {
        writerRepository.update(entity);
    }

    public void remove(Long id) {
        writerRepository.remove(id);
    }

    public List<Writer> getAll() {
        return writerRepository.getAll();
    }

    public boolean writerNameContains(String s) {
        return getAll()
                .stream()
                .anyMatch(writer -> writer.getWriterName().equals(s));
    }

    public boolean containsId(Long id) {
        return getAll()
                .stream()
                .anyMatch(writer -> writer.getId().equals(id));
    }

    public Writer getById(Long id) {
        return get(id);
    }

    public Writer getByName(String s) {
        return getAll()
                .stream()
                .filter(writer -> writer.getWriterName().equals(s))
                .findAny()
                .orElseThrow();
    }

    public void delete(Writer w) {
        remove(w.getId());
    }
}
