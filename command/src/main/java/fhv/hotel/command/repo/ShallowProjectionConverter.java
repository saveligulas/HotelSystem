package fhv.hotel.command.repo;

import fhv.hotel.command.model.domain.IShallowProjection;

import java.util.ArrayList;
import java.util.List;

public class ShallowProjectionConverter {
    public static <T, ID> T buildShallowModel(IShallowProjection<T, ID> model) {
        return model.buildShallowModel(model.getID());
    }

    public static <T, ID> List<T> buildShallowModelList(List<IShallowProjection<T, ID>> model) {
        List<T> targetList = new ArrayList<>();
        for (IShallowProjection<T, ID> item : model) {
            targetList.add(buildShallowModel(item));
        }
        return targetList;
    }
}
