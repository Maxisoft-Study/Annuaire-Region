package maxime.Utils;

import maxime.BaseClass.Annuaire;
import maxime.Const;
import maxime.ResourceLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConvertXMLToSer implements Const {

    public static void main(String[] args) throws InterruptedException, IOException {

        String[] files = new String[]{"regions", "departements", "communes"};

        String xml = ".xml";
        String res = ".ser";

        final ResourceLoader resourceLoader = new ResourceLoader(RESOURCE_DIR);

        resourceLoader.fastLoad(files[0] + xml, files[1] + xml, files[2] + xml);

        Annuaire[] annuaires = new Annuaire[]{resourceLoader.getRegionAnnuaire(), resourceLoader.getDepartementAnnuaire(), resourceLoader.getCommuneAnnuaire()};

        for (int i = 0; i < annuaires.length; ++i) {
            String file = files[i];
            Annuaire annuaire = annuaires[i];
            Path path = Paths.get(RESOURCE_DIR, file + res);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toFile()));
            out.writeObject(annuaire);
            out.close();
        }

    }
}
