package maxime;

import java.awt.*;

public interface Const {
    public final static String BASE_WORKING_DIR = System.getProperty("user.dir");
    public final static String RESOURCE_DIR = "src/main/resources/";
    public final static boolean DEBUG = true;
    public final static int IMG_SCALE_BIT = Image.SCALE_SMOOTH;
    public final static int MAX_IMG_WIDTH = 500;
    public final static int MAX_IMG_HEIGHT = 500;
}
