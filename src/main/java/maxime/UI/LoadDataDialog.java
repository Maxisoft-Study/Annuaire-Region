package maxime.UI;

import com.alee.extended.layout.TableLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.progressbar.WebProgressBar;
import com.alee.laf.rootpane.WebDialog;

import java.awt.*;

public class LoadDataDialog extends WebDialog {
    public LoadDataDialog(Window owner) {
        super(owner, "Regions - Chargement");
        setIconImages(WebLookAndFeel.getImages());
        setDefaultCloseOperation(WebDialog.DO_NOTHING_ON_CLOSE);
        setShowCloseButton(false);
        setResizable(false);
        setModal(true);

        TableLayout layout = new TableLayout(new double[][]{{TableLayout.PREFERRED, TableLayout.FILL},
                {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}});
        layout.setHGap(5);
        layout.setVGap(5);
        WebPanel content = new WebPanel(layout);
        content.setMargin(15, 30, 15, 30);
        content.setOpaque(false);

        WebProgressBar progressBar = new WebProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Chargement des données ...");
        content.add(progressBar, "0,0");

        add(content);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
