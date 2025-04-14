package fhv.hotel.core.repo;

public interface IBasicRepository<T, ID> {
    T findById(ID id);
    void save(T t);
    void update(T t);
}
