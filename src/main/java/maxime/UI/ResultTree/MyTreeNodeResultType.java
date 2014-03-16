package maxime.UI.ResultTree;

import maxime.UI.ResultTree.WrappedBaseClass.StringWrapper;

public class MyTreeNodeResultType extends MyTreeNode {
    public MyTreeNodeResultType(String value) {
        super(new StringWrapper(value), true);
    }
}
