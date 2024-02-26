package com.rest.restapi.service;

import org.springframework.stereotype.Service;

// Import the relevant libraries
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class AddTextService {
    // Choose folder where images to add text to are
    String projectRoot = System.getProperty("user.dir");
    private final String  BASE_IMAGE_OUTPUT_PATH = projectRoot+"/images/";

    public String addTextToImage(File imageFile, String textToAdd) {
        try {
            return processImage(imageFile, textToAdd);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("imageFile.getAbsolutePath(): ");
            System.out.print(imageFile.getAbsolutePath());
            return imageFile.getAbsolutePath();
        }
    }

    // Global function which incorporates images loading, adding of text and saving of final instagram post to folder
    private String processImage(File imageFile, String textToAdd) throws IOException {
        //Read the image from the file
        BufferedImage imageToEdit = loadImageToEdit(imageFile);

        // Perform the Text Addition
        BufferedImage finalPost = textAdd(imageToEdit, textToAdd);

        // Save the finished post to a new file
        File outputFolder = new File(BASE_IMAGE_OUTPUT_PATH);
        if (!outputFolder.exists()){
            outputFolder.mkdirs();
        }
        String finalPostFileName = String.format("final_post%s.jpg", UUID.randomUUID());
        File finalPostFile = new File(String.format(outputFolder +"/"+ finalPostFileName));
        ImageIO.write(finalPost, "jpg", finalPostFile);
        System.out.println("Text addition complete. Output saved to: "+finalPostFile.getAbsolutePath());
        return finalPostFileName;
    }

    // Function to load the image to edit
    private static BufferedImage loadImageToEdit(File imageFile) {
        try{
            return ImageIO.read(imageFile);
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    // Function which adds text to the image to edit
    private static BufferedImage textAdd(BufferedImage image, String textToAdd) {

        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new BufferedImage with the same dimensions and type
        BufferedImage finalPost = new BufferedImage(width, height, image.getType());

        // Create a Graphics object to draw on the image
        Graphics graphics = finalPost.getGraphics();

        // Draw the original image onto the new image
        graphics.drawImage(image, 0, 0, null);

        // Set the font, font size and color for the text
        int FONT_SIZE = 30;
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE);
        Color textColor = Color.WHITE;

        graphics.setFont(font);
        graphics.setColor(textColor);

        // Calculate the position to center the text
        int textWidth = graphics.getFontMetrics().stringWidth(textToAdd);
        int x = (width - textWidth) / 2;
        int y = height / 2;

        // Make text spread on multiple lines if it is too wide for the image
        if (textWidth > 0.9 * width) {
            int numberOfLines = (int)(Math.ceil((double) textWidth / (0.9 * width)));
            int middleLine = (int)Math.floor(numberOfLines/2);

            int lineHeight = graphics.getFontMetrics().getHeight();

            String[] linesToAdd = lineSplit(textToAdd, width,textWidth,(int)numberOfLines, FONT_SIZE);

            for (int i = 0; i < numberOfLines; i++) {
                textWidth = graphics.getFontMetrics().stringWidth(linesToAdd[i]);
                x = (width - textWidth) / 2;
                int yOffset = y + (i-middleLine) * lineHeight;
                graphics.drawString(linesToAdd[i], x, yOffset);
            }
        }
        else {
            // Draw the text onto the image if it fits on one line
            graphics.drawString(textToAdd, x, y);
        }

        // Dispose of the Graphics object to free resources
        graphics.dispose();

        return finalPost;
    }

    // Function to split overflowing text into multiple segments to add instead of overflowing on one line
    private static String[] lineSplit(String paragraphToSplit, int imageWidth, int textWidth, int numberOfLines, int FONT_SIZE){
        String[] linesToAdd = new String[numberOfLines];

        // 3630 = constant that works well
        int lineLength = (int)Math.round(3630/(2.2*FONT_SIZE));

        int indexAdjustCount = 0;

        for(int i=0;i<numberOfLines;i++){
            int startIndex = i*lineLength-indexAdjustCount;
            int endIndex = (i+1)*lineLength;

            indexAdjustCount=0;

            // Avoid out of bounds when searching through paragraph indexes
            endIndex = Math.min(endIndex, paragraphToSplit.length());

            // Find the last space within the specified character limit
            while(endIndex < paragraphToSplit.length() && paragraphToSplit.charAt(endIndex) != ' '){
                endIndex--;
                indexAdjustCount++;
            }

            // Extract the substring for each line
            if (startIndex < endIndex) {
                linesToAdd[i] = paragraphToSplit.substring(startIndex, endIndex).trim();
                startIndex = endIndex;
            }
            else {
                linesToAdd[i] = ""; // Empty string for invalid indices
            }
        }
        return linesToAdd;
    }
}


