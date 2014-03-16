package maxime.Utils;

import maxime.BaseClass.BaseClass;

import java.util.Comparator;

public class BaseClassComparator implements Comparator<BaseClass> {
    @Override
    public int compare(BaseClass o1, BaseClass o2) {
        return o1.getId() - o2.getId();
    }
}
