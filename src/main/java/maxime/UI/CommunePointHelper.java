package maxime.UI;

import com.alee.extended.image.WebImage;
import maxime.BaseClass.Commune;
import maxime.BaseClass.Region;
import maxime.ResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Une factory de point a mettre sur la carte
 */
public class CommunePointHelper {
    private int imageWidth;
    private int imageHeight;
    private Rectangle imgbound;
    private Region region;
    private ResourceLoader loader;
    private BufferedImage pointImg;

    public CommunePointHelper(ResourceLoader loader) {
        this.loader = loader;
        this.pointImg = loader.loadImage("orientation.png");
    }

    public CommunePoint createCommunePoint(final Commune commune) {
        CommunePoint ret = new CommunePoint(commune);

        int basex = imgbound.x + 5;
        basex -= pointImg.getWidth() / 2; //centre
        int basey = imgbound.y + 5;
        basey -= pointImg.getHeight();


        float tmp = commune.getLongitude() - region.getCarte_longitude_haut_gauche();
        float tmp2 = region.getCarte_longitude_bas_droite() - region.getCarte_longitude_haut_gauche();
        float ratio = tmp / tmp2;
        int x = basex;
        x += ratio * imageWidth;

        tmp = commune.getLatitude() - region.getCarte_latitude_haut_gauche();
        tmp2 = region.getCarte_latitude_bas_droite() - region.getCarte_latitude_haut_gauche();
        ratio = tmp / tmp2;
        int y = basey;
        y += ratio * imageHeight;

        Rectangle bounds = new Rectangle(x, y, pointImg.getWidth(), pointImg.getHeight());
        //System.out.println(commune);
        //System.out.println(bounds);
        ret.setBounds(bounds);
        return ret;
    }

    public Rectangle getImgbound() {
        return imgbound;
    }

    public void setImgbound(Rectangle imgbound) {
        this.imgbound = imgbound;
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public class CommunePoint extends WebImage {
        private Commune commune;

        private CommunePoint(Commune commune) {
            super(pointImg);
            this.commune = commune;
        }

        public Commune getCommune() {
            return commune;
        }
    }


}
