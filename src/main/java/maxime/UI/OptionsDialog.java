package maxime.UI;

import com.alee.extended.button.WebSwitch;
import com.alee.extended.layout.TableLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.spinner.WebSpinner;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsDialog extends WebDialog {
    public OptionsDialog(final MainFrame owner) {
        super(owner, "Options");

        setIconImages(WebLookAndFeel.getImages());
        setDefaultCloseOperation(WebDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);

        TableLayout layout = new TableLayout(new double[][]{{TableLayout.PREFERRED, TableLayout.FILL},
                {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}});
        layout.setHGap(5);
        layout.setVGap(5);
        WebPanel content = new WebPanel(layout);
        content.setMargin(15, 30, 15, 30);
        content.setOpaque(false);


        content.add(new WebLabel("Activer l'animation lors des changements de region : "), "0,0");
        content.add(new WebSwitch(owner.isEnableAnimation()) {
            {
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        owner.setEnableAnimation(isSelected());
                    }
                });
            }
        }, "1,0");

        content.add(new WebLabel("Limite développement automatique des resultats  : "), "0,1");
        content.add(new WebSpinner() {
            {
                setValue(owner.getAutoExpendResultNbr());
                addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        owner.setAutoExpendResultNbr(Integer.valueOf(getValue().toString()));
                    }
                });
            }
        }, "1,1");
        add(content);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
