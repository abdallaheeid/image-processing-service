package org.abdallah.imageprocessingservice.transformations;

import org.abdallah.imageprocessingservice.dto.TransformRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ImageTransformService {

    public byte[] transform(String inputPath, TransformRequest request, String targetFormat) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputPath));

        if (request.getResize() != null) {
            image = resize(image, request.getResize().getWidth(), request.getResize().getHeight());
        }

        if (request.getCrop() != null) {
            image = crop(
                    image,
                    request.getCrop().getX(),
                    request.getCrop().getY(),
                    request.getCrop().getWidth(),
                    request.getCrop().getHeight()
            );
        }

        if (request.getRotate() != null) {
            image = rotate(image, request.getRotate());
        }

        if (request.getFilters() != null) {
            if (Boolean.TRUE.equals(request.getFilters().getGrayscale())) {
                image = grayscale(image);
            }
            if (Boolean.TRUE.equals(request.getFilters().getSepia())) {
                image = sepia(image);
            }
        }

        if (targetFormat.equalsIgnoreCase("jpeg")) {
            targetFormat = "jpg";
        }
        if (targetFormat.equalsIgnoreCase("jfif")) {
            targetFormat = "jpg";
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean ok = ImageIO.write(image, targetFormat, baos);
        if (!ok) {
            throw new IOException("ImageIO could not write format: " + targetFormat);
        }
        return baos.toByteArray();
    }

    private BufferedImage resize(BufferedImage src, int width, int height) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(src, 0, 0, width, height, null);
        g.dispose();

        return out;
    }

    private BufferedImage crop(BufferedImage src, int x, int y, int w, int h) {
        x = Math.max(0, x);
        y = Math.max(0, y);
        w = Math.min(w, src.getWidth() - x);
        h = Math.min(h, src.getHeight() - y);
        return src.getSubimage(x, y, w, h);
    }

    private BufferedImage rotate(BufferedImage src, int degrees) {
        double radians = Math.toRadians(degrees);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int w = src.getWidth();
        int h = src.getHeight();
        int newW = (int) Math.floor(w * cos + h * sin);
        int newH = (int) Math.floor(h * cos + w * sin);

        BufferedImage result = new BufferedImage(newW, newH, src.getType());
        Graphics2D g2 = result.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newW - w) / 2.0, (newH - h) / 2.0);
        at.rotate(radians, w / 2.0, h / 2.0);
        g2.setTransform(at);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return result;
    }

    private BufferedImage grayscale(BufferedImage src) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return op.filter(src, null);
    }

    private BufferedImage sepia(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int p = src.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int tr = (int)(0.393*r + 0.769*g + 0.189*b);
                int tg = (int)(0.349*r + 0.686*g + 0.168*b);
                int tb = (int)(0.272*r + 0.534*g + 0.131*b);

                r = Math.min(255, tr);
                g = Math.min(255, tg);
                b = Math.min(255, tb);

                out.setRGB(x, y, (a<<24) | (r<<16) | (g<<8) | b);
            }
        }
        return out;
    }
}
