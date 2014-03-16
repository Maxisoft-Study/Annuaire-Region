package maxime.UI.ResultTree.WrappedBaseClass;

import maxime.BaseClass.BaseClass;
import maxime.BaseClass.Region;

public class WrappedRegion extends WrappedBaseClass {
    final private Region region;

    public WrappedRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region.getNom();
    }

    @Override
    public BaseClass getContent() {
        return this.region;
    }
}
