package fhv.hotel.command.model.domain;

public interface IShallowProjection<M, ID> {
    boolean isShallow();
    M buildShallowModel(ID id);
    ID getID();
}
