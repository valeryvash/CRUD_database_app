package service;

import model.Writer;
import repository.WriterRepository;

import java.util.List;

import static util.EntitiesIdCheck.*;

public class WriterService {
    private WriterRepository writerRepository;

    private WriterService() {
    }

    public WriterService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    public Writer add(Writer entity) {
        throwIfIdIsPositive(entity);
        return writerRepository.add(entity);
    }

    public Writer get(Long id) {
        throwIfIdIsSmallerThanOne(id);
        return writerRepository.get(id);
    }

    public Writer update(Writer entity) {
        throwIfIdIsSmallerThanOne(entity);
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
        return writerRepository.getByName(s);
    }

    public void delete(Writer w) {
        remove(w.getId());
    }
}
