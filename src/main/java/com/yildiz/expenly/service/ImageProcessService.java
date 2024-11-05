package com.yildiz.expenly.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class ImageProcessService {

    public BufferedImage resize(BufferedImage img, double scaleFactor) {
        int newWidth = (int) (img.getWidth() * scaleFactor);
        int newHeight = (int) (img.getHeight() * scaleFactor);

        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public BufferedImage preprocessImage(BufferedImage img) {
        // 2.5 çarpanı ile boyutlandırma (örneğin 2 veya 3 olarak ayarlanabilir)
        BufferedImage resizedImage = resize(img, 2.5);
        BufferedImage grayscaleImage = setGrayscale(resizedImage);
        return removeNoise(grayscaleImage);
    }

    private BufferedImage setGrayscale(BufferedImage img) {
        BufferedImage grayscale = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    private BufferedImage removeNoise(BufferedImage img) {
        int threshold = 162; // Eşik değeri
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int pixel = img.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                if (red < threshold && green < threshold && blue < threshold) {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return img;
    }
}
