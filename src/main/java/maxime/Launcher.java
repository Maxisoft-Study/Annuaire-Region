package maxime;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.optionpane.WebOptionPane;
import maxime.UI.LoadDataDialog;
import maxime.UI.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class Launcher implements Const {
    private static final ResourceLoader resourceLoader = new ResourceLoader(RESOURCE_DIR);

    public static void main(String[] args) throws InterruptedException {
        // You should work with UI (including installing L&F) inside Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Install WebLaF as application L&F
                WebLookAndFeel.install();
                resourceLoader.isRunningFromJar();
                final LoadDataDialog loadDataDialog = new LoadDataDialog(null);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (DEBUG) {
                                resourceLoader.fastLoad("regions.ser", "departements.ser", "communes.ser");
                            } else { // chargement avec fichier XML (plus lent)
                                resourceLoader.fastLoad("regions.xml", "departements.xml", "communes.xml");
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                            WebOptionPane.showMessageDialog(loadDataDialog, "Erreur lors du chargement des données : \n" + e.getMessage(), "Error", WebOptionPane.ERROR_MESSAGE);
                            loadDataDialog.setVisible(false);
                            loadDataDialog.dispose();
                            return;
                        }
                        loadDataDialog.setVisible(false);
                        loadDataDialog.dispose();


                        boolean decorateFrames = WebLookAndFeel.isDecorateFrames();
                        WebLookAndFeel.setDecorateFrames(true);
                        MainFrame mf;
                        try {
                            mf = new MainFrame(resourceLoader);
                        } catch (IOException e) {
                            System.out.println(e);
                            WebOptionPane.showMessageDialog(null, "Erreur dans l'app \n" + e.getMessage(), "Error", WebOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        mf.setVisible(true);
                        // Restoring frame decoration option
                        WebLookAndFeel.setDecorateFrames(decorateFrames);

                    }
                });
                thread.start();
                loadDataDialog.setVisible(true);

            }
        });
    }
}