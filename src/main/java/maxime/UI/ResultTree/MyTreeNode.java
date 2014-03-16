package maxime.UI.ResultTree;


import maxime.UI.ResultTree.WrappedBaseClass.WrappedBaseClass;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class MyTreeNode extends DefaultMutableTreeNode {

    protected MyTreeNode(WrappedBaseClass userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    public WrappedBaseClass getUserObject() {
        return (WrappedBaseClass) super.getUserObject();
    }
}
