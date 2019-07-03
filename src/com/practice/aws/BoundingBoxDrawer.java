package com.practice.aws;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.amazonaws.services.rekognition.model.BoundingBox;

public class BoundingBoxDrawer {

    public void drawBoundingBoxes(byte[] bytes, String orientationCorrection, List<BoundingBox> boundingBoxes) {
        int width;
        int height;
        BufferedImage img;
        Graphics2D graphics;
        try {
            img = ImageIO.read(new ByteArrayInputStream(bytes));
            width = img.getWidth();
            height = img.getHeight();
            graphics = img.createGraphics();
        } catch (IOException e) {
            System.err.println("Failed to read image: " + e.getLocalizedMessage());
            return;
        }
        System.out.println("Image: width=" + width + ", height=" + height);
        
        for (BoundingBox bb : boundingBoxes) {

            drawBoundingBox(bb, orientationCorrection, width, height, graphics);
        }
        
        try {
            ImageIO.write(img, "jpg", new File("img_bb.jpg"));
        } catch (IOException e) {
            System.err.println("Failed to write image: " + e.getLocalizedMessage());
        }
    }

    private void drawBoundingBox(BoundingBox bb, String orientationCorrection, int width, int height,
            Graphics2D graphics) {
        
        BoundingBox cbb = convertBoundingBox(bb, orientationCorrection, width, height);
        if (cbb == null) {
            return;
        }
        
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawRect(cbb.getLeft().intValue(), cbb.getTop().intValue(), 
                cbb.getWidth().intValue(), cbb.getHeight().intValue());
    }
    
    private BoundingBox convertBoundingBox(BoundingBox bb, String orientationCorrection, int width, int height) {
        if (orientationCorrection == null) {
            System.out.println("No orientationCorrection available.");
            return null;
        } else {
            float left = -1;
            float top = -1;
            switch (orientationCorrection) {
            case "ROTATE_0":
                left = width * bb.getLeft();
                top = height * bb.getTop();
                break;
            case "ROTATE_90":
                left = height * (1 - (bb.getTop() + bb.getHeight()));
                top = width * bb.getLeft();
                break;
            case "ROTATE_180":
                left = width - (width * (bb.getLeft() + bb.getWidth()));
                top = height * (1 - (bb.getTop() + bb.getHeight()));
                break;
            case "ROTATE_270":
                left = height * bb.getTop();
                top = width * (1 - bb.getLeft() - bb.getWidth());
                break;
            default:
                System.out.println("Orientation correction not supported: " + 
                        orientationCorrection);
                return null;
            }
            System.out.println("BoundingBox: left=" + (int)left + ", top=" + 
                    (int)top + ", width=" + (int)(bb.getWidth()*width) + 
                    ", height=" + (int)(bb.getHeight()*height));
            BoundingBox outBB = new BoundingBox();
            outBB.setHeight(bb.getHeight()*height);
            outBB.setWidth(bb.getWidth()*width);
            outBB.setLeft(left);
            outBB.setTop(top);
            return outBB;
        }
    }
}
