package com.rest.restapi.controller;

import com.rest.restapi.service.AddTextService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/addText")
public class AddTextController {
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
    private final AddTextService addTextService;

    public AddTextController(AddTextService addTextService) {
        this.addTextService = addTextService;
    }

    @GetMapping("/image/{filename}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Resource resource = new ClassPathResource("/images/"+filename);
        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @PostMapping("/upload")
    public String uploadImage(
        Model model,
        @RequestParam("image") MultipartFile file,
        @RequestParam(name = "textToAdd") String textToAdd
    ) {
        try {
            String updatedImgName = saveAndProcessImage(file, textToAdd);
            model.addAttribute("filename", updatedImgName);
            model.addAttribute("msg", "Uploaded image! Upload another?");
        } catch (IOException | InterruptedException e) {
            // Handle the exception appropriately
            model.addAttribute("error", "An error occurred while processing the image.");
            e.printStackTrace(); // Log the exception for debugging purposes
        }
        return "uploadIndex";
    }
    
    private String saveAndProcessImage(MultipartFile file, String textToAdd) throws IOException, InterruptedException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIRECTORY, fileName);
        createUploadDirectoryIfNeeded();
    
        Files.write(filePath, file.getBytes());
    
        String updatedImgName = addTextService.addTextToImage(new File(filePath.toString()), textToAdd);
        System.out.println("filename: " + updatedImgName);
        return updatedImgName;
    }


    private void createUploadDirectoryIfNeeded() throws IOException {
        if (!Files.exists(Paths.get(UPLOAD_DIRECTORY))) {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            System.out.println("Folder created successfully.");
        } else {
            System.out.println("Folder already exists.");
        }
    }
}
