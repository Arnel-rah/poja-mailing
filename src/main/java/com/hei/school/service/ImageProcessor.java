package com.hei.school.service;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor {

  private ImageProcessor() {}

  public static byte[] toGrayscale(File imageFile) throws IOException {
    BufferedImage original = ImageIO.read(imageFile);
    if (original == null) {
      throw new IOException("Impossible de lire l'image : " + imageFile.getName());
    }

    BufferedImage grayscale =
        new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

    Graphics g = grayscale.getGraphics();
    g.drawImage(original, 0, 0, null);
    g.dispose();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(grayscale, "png", out);
    return out.toByteArray();
  }
}
