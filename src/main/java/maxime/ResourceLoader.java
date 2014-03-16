package maxime;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import maxime.BaseClass.*;
import maxime.Utils.BaseClassComparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceLoader implements Const {
    private Annuaire<Region> regionAnnuaire;
    private Annuaire<Commune> communeAnnuaire;
    private Annuaire<Departement> departementAnnuaire;
    private List<Departement> departementsSortedByNumero;

    private XStream xstream;
    private HashMap<String, BufferedImage> loadedimages;
    final private Path basePath;

    public ResourceLoader(String basePath) {
        this.basePath = Paths.get(basePath);

        this.xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(Annuaire.class);
        xstream.processAnnotations(Departement.class);
        xstream.processAnnotations(Commune.class);
        xstream.processAnnotations(Region.class);

        loadedimages = new HashMap<String, BufferedImage>();
        this.departementsSortedByNumero = new ArrayList<Departement>();
    }

    public boolean isRunningFromJar(){
        String className = this.getClass().getName().replace('.', '/');
        String classJar =
                this.getClass().getResource("/" + className + ".class").toString();
        System.out.println(classJar);
        return classJar.startsWith("jar:");
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseClass> Annuaire<T> loadAnnuaire(String filepath) {
        Path path = resolveFile(filepath);
        Annuaire<T> ret;
        if (path.toString().endsWith(".xml")) {
            ret = (Annuaire<T>) this.xstream.fromXML(path.toFile());
        } else if (path.toString().endsWith(".ser")) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toFile()));
                ret = (Annuaire<T>) in.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // pas de bonne extension
            throw new RuntimeException("Seul les fichiers xml et ser sont autorisé.");
        }
        ret.sort();
        for (BaseClass object : ret.getContent()) {
            object.postParseSetId();
        }
        return ret;
    }

    private Path resolveFile(String filepath) {
        return this.basePath.resolve(filepath);
    }

    public Annuaire<Region> loadRegions(String filepath) {
        if (this.regionAnnuaire != null) {
            throw new IllegalStateException("Un annuaire de Regions est deja chargé");
        }
        this.regionAnnuaire = loadAnnuaire(filepath);
        return this.getRegionAnnuaire();
    }

    public Annuaire<Departement> loadDepartements(String filepath) {
        if (this.departementAnnuaire != null) {
            throw new IllegalStateException("Un annuaire de Departements est deja chargé");
        }
        this.departementAnnuaire = loadAnnuaire(filepath);

        this.departementsSortedByNumero.addAll(this.departementAnnuaire.getContent());
        Collections.sort(this.departementsSortedByNumero, new DepartementComparatorOnCode());

        return this.getDepartementAnnuaire();
    }

    public Annuaire<Commune> loadCommunes(String filepath) {
        if (this.communeAnnuaire != null) {
            throw new IllegalStateException("Un annuaire de Communes est deja chargé");
        }

        this.communeAnnuaire = loadAnnuaire(filepath);
        return this.getCommuneAnnuaire();
    }

    /**
     * Raccourcit pour charger les fichiers.
     * Utilise des threads pour charger chaque fichier.
     *
     * @param region      le path du fichier contenant les regions a parser
     * @param departement le path du fichier contenant les departements a parser
     * @param communes    le path du fichier contenant les communes a parser
     * @throws InterruptedException
     */
    public void fastLoad(final String region, final String departement, final String communes) throws InterruptedException {
        //contient des exceptions lancer par des threads
        final Queue<Throwable> exceptions = new ConcurrentLinkedQueue<Throwable>();

        // l'exception handler des threads que l'on va crée/lancer
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                exceptions.add(ex);
            }
        };

        //les threads
        Thread[] threads = new Thread[]{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResourceLoader.this.loadDepartements(departement);
                    }
                }, "Departements load Thread"),

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResourceLoader.this.loadCommunes(communes);
                    }
                }, "Communes load Thread"),

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResourceLoader.this.loadRegions(region);
                    }
                }, "Regions load Thread")
        };
        //on lance les threads
        for (Thread thread : threads) {
            thread.setUncaughtExceptionHandler(exceptionHandler); //change le gestionnaire d'exceptions par defaut
            thread.start();
        }

        //wait thread
        for (Thread thread : threads) {
            thread.join();
        }

        //regarde si exceptions
        for (Throwable throwable : exceptions) {
            throw new RuntimeException(throwable);
        }

        //finalisation apres chargement depuis les fichiers
        this.linksLoadedObject();
    }

    public BufferedImage loadImage(String filepath) {
        Path path = Paths.get(BASE_WORKING_DIR).toAbsolutePath();
        path = path.relativize(resolveFile(filepath).toAbsolutePath());

        BufferedImage ret = this.loadedimages.get(path.toString());
        if (ret == null) {
            Path path2 = resolveFile(filepath);
            try {
                ret = ImageIO.read(path2.toFile());
            } catch (IOException e) {
                throw new RuntimeException("Pas de fichier " + filepath, e); //TODO
            }
            this.loadedimages.put(path.toString(), ret);
        }
        return ret;
    }


    public void linksLoadedObject() {

        final Annuaire<Commune> communeAnnuaire1 = getCommuneAnnuaire();
        final Annuaire<Region> regionAnnuaire1 = getRegionAnnuaire();
        final Annuaire<Departement> departementAnnuaire1 = getDepartementAnnuaire();

        for (Commune commune : communeAnnuaire1.getContent()) {
            commune.postParse(this);
        }
        for (Departement departement : departementAnnuaire1.getContent()) {
            departement.postParse(this);
        }
        for (Region region : regionAnnuaire1.getContent()) {
            region.postParse(this);
        }
        System.gc();
    }

    /**
     * commune n'utilise pas d'id ref.
     *
     * @param code
     * @return
     */
    public Departement findDepartement(final String code) {
        //on effectue une recherche avec l'algo binaire de recherche (comme l'annuaire est trié en fonction des id)
        int result = Collections.binarySearch(this.departementsSortedByNumero, new DepartementCodeSetter(code), new DepartementComparatorOnCode());
        if (result < 0) {
            throw new IllegalAccessError(String.format("le code \"%s\" n'existe pas", code));
        }
        Departement ret = this.departementsSortedByNumero.get(result);
        return ret;
    }

    private <T extends BaseClass> T findItemByID(Annuaire<T> annuaire, final int id) {
        //on effectue une recherche avec l'algo binaire de recherche (comme l'annuaire est trié en fonction des id)
        int result = Collections.binarySearch(annuaire.getContent(), new BaseClassIdSetter(id), new BaseClassComparator());
        if (result < 0) {
            throw new IllegalAccessError(String.format("l'id \"%s\" n'existe pas", id));
        }
        T ret = annuaire.getContent().get(result);
        return ret;
    }

    public Region findRegion(final int id) {
        return findItemByID(this.getRegionAnnuaire(), id);
    }

    public Commune findCommune(final int id) {
        return findItemByID(this.getCommuneAnnuaire(), id);
    }


    public Annuaire<Region> getRegionAnnuaire() {
        return regionAnnuaire;
    }

    public Annuaire<Commune> getCommuneAnnuaire() {
        if (this.communeAnnuaire == null) {
            throw new IllegalStateException("l'annuaire de Communes n'est pas chargé");
        }
        return communeAnnuaire;
    }

    public Annuaire<Departement> getDepartementAnnuaire() {
        if (this.departementAnnuaire == null) {
            throw new IllegalStateException("l'annuaire de Departements n'est pas chargé");
        }
        return departementAnnuaire;
    }

    public Path getBasePath() {
        return basePath;
    }

    private static class DepartementComparatorOnCode implements Comparator<Departement> {
        @Override
        public int compare(Departement o1, Departement o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
    }

    private static class BaseClassIdSetter extends BaseClass {
        private final int id;

        // on crée un object avec l'id donnée en parametre
        public BaseClassIdSetter(int id) {
            this.id = id;
        }

        @Override
        public void postParse(ResourceLoader resourceLoader) {

        }

        @Override
        public Region getRegion() {
            return null;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public float getSuperficie() {
            return 0;
        }

        @Override
        public int getPopulation() {
            return 0;
        }

        @Override
        public float getDensite() {
            return 0;
        }
    }

    private static class DepartementCodeSetter extends Departement {
        private final String code;

        public DepartementCodeSetter(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }
    }
}
