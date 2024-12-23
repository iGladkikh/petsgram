package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.service.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

//    @GetMapping("/posts/{postId}/images")
//    public List<Image> getPostImages(@PathVariable("postId") long postId) {
//        return imageService.getPostImages(postId);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts/{postId}/images")
    public List<Image> addPostImages(@PathVariable("postId") long postId,
                                     @RequestParam("image") List<MultipartFile> files) {
        return imageService.saveImages(postId, files);
    }

    @GetMapping(value = "/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadImage(@PathVariable long imageId) {
        Optional<Image> imageOptional = imageService.findById(imageId);
        if (imageOptional.isEmpty()) {
            throw new NotFoundException("Image with id " + imageId + " not found");
        }

        //ImageData imageData = imageService.getImageData(imageId);
        Image image = imageOptional.get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                //ContentDisposition.attachment()
                ContentDisposition.inline()
                        .filename(image.getOriginalFileName())
                        .build()
        );

        //return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
        try {
            return new ResponseEntity<>(Files.readAllBytes(Path.of(image.getFilePath())), headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
