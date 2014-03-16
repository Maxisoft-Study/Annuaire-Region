package maxime.UI;

import com.alee.extended.layout.TableLayout;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.rootpane.WebDialog;
import com.alee.laf.text.WebTextField;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdvancedSearchDialog extends WebDialog {
    private WebTextField textField;
    private String data;

    public String getData() {
        return data;
    }

    private void actionPerformed() {
        AdvancedSearchDialog.this.data = textField.getText();
        AdvancedSearchDialog.this.setVisible(false);
        AdvancedSearchDialog.this.dispose();
    }

    public AdvancedSearchDialog(MainFrame owner) {
        super(owner, "Recherche de commune avancée");

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


        content.add(new WebLabel(String.format("Recherche les communes de %s dont la population est supérieure au seuil suivant :", owner.getLastRegion().getNom())), "0,0");
        textField = new WebTextField("", 1) {
            {
                setInputPrompt("seuil population");
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AdvancedSearchDialog.this.actionPerformed();
                    }
                });
            }
        };
        content.add(textField, "0,1");
        content.add(new WebButton("Rechercher", new ImageIcon(owner.getResourceLoader().loadImage("search.png"))) {
            {
                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AdvancedSearchDialog.this.actionPerformed();
                    }
                });
            }
        }, "0,2");
        add(content);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
