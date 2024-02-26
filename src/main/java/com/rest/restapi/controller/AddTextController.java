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
    private static final AddTextService addTextService = new AddTextService();

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
        ) throws IOException, InterruptedException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());

        createUploadDirectoryIfNeeded();

        Files.write(fileNameAndPath, file.getBytes());
        String updatedImgName = addTextService.addTextToImage(new File(UPLOAD_DIRECTORY + "/" + file.getOriginalFilename()), textToAdd);
        System.out.println("filename: "+updatedImgName);
        model.addAttribute("filename", updatedImgName);
        model.addAttribute("msg", "Uploaded image! Upload another?");
        return "uploadIndex";
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


