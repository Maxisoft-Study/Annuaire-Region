package maxime.UI.ResultTree;

import com.alee.laf.tree.WebTreeCellRenderer;
import maxime.ResourceLoader;

import javax.swing.*;

public class MyTreeCellRenderer extends WebTreeCellRenderer {
    public MyTreeCellRenderer(ResourceLoader resourceLoader) {
        super();
        this.setLeafIcon(new ImageIcon(resourceLoader.loadImage("orientation.png")));
        this.setRootIcon(new ImageIcon(resourceLoader.loadImage("unix.png")));
    }

    /*@Override
    public WebTreeElement getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, hasFocus);
    }*/
}
