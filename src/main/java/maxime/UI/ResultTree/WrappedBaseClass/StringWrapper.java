package maxime.UI.ResultTree.WrappedBaseClass;

import maxime.BaseClass.BaseClass;

public class StringWrapper extends WrappedBaseClass {
    private String string;

    public StringWrapper(String string) {
        this.string = string;
    }


    @Override
    public String toString() {
        return string;
    }

    @Override
    public BaseClass getContent() {
        return null;
    }
}
