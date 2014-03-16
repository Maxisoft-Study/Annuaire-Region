package maxime;

import maxime.SearchProcessor.SearchProcessor;

public class testSerial implements Const {

    public static void main(String[] args) throws InterruptedException {

        final ResourceLoader resourceLoader = new ResourceLoader(RESOURCE_DIR);


        long time = System.currentTimeMillis();
        if (DEBUG) {
            resourceLoader.fastLoad("regions.ser", "departements.ser", "communes.ser");
        } else { // chargement avec fichier XML (2x plus lent)
            resourceLoader.fastLoad("regions.xml", "departements.xml", "communes.xml");
        }
        System.out.println("Loading Time = " + (System.currentTimeMillis() - time) / 1000D);

        SearchProcessor searchProcessor = new SearchProcessor(resourceLoader);
        System.out.println(searchProcessor.searchForDepartements("doù"));
        System.out.println(searchProcessor.searchForCommune("doù"));

        //System.out.println(resourceLoader.findDepartement("95"));
    }
}
