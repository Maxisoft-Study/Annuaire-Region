package maxime.Utils;

import maxime.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.Normalizer;

public final class Utils implements Const {
    public static String SimplyfyString(final String s) {
        String ret = Normalizer.normalize(s, Normalizer.Form.NFD);
        ret = ret.replaceAll("\\p{M}", "");
        return ret.toLowerCase();
    }

    public static Image ResizeImage(final BufferedImage image) {
        final Dimension imgdim = new Dimension(image.getWidth(), image.getHeight());
        final double ratio = imgdim.getWidth() / image.getHeight();
        Dimension retdim = new Dimension(imgdim);
        boolean sucesscalc = false;
        int heightguard = MAX_IMG_HEIGHT;
        while (!sucesscalc) {
            if (retdim.getWidth() > MAX_IMG_WIDTH) {
                retdim.width = MAX_IMG_WIDTH;
                retdim.height = (int) (MAX_IMG_WIDTH / ratio);
            } else if (retdim.getHeight() > MAX_IMG_HEIGHT) {
                retdim.height = heightguard;
                retdim.width = (int) (heightguard * ratio);
                heightguard = heightguard / 2 + heightguard / 4; // 75 %
            } else {
                sucesscalc = true;
            }
        }


        Image ret = image.getScaledInstance(retdim.width, retdim.height, IMG_SCALE_BIT);
        return ret;
    }

    private Utils() {
    }
}
