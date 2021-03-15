package com.project.SLX.service;

import com.project.SLX.model.Image;
import com.project.SLX.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image getById(Long imageId) {
        Optional<Image> image  = imageRepository.findById(imageId);
        image.orElseThrow();

        return image.get();
    }

    public Long save(Image image) {
        return imageRepository.save(image).getImageId();
    }

    public void deleteById(Long imageId) {
        Optional<Image> image  = imageRepository.findById(imageId);
        image.orElseThrow();
        imageRepository.deleteById(imageId);
    }

    public byte[] getData(Long imageId) {
        Image image;
        byte[] imageData;

        try {
            image = this.getById(imageId);

            Byte[] originalData = image.getImage();
            imageData = new byte[originalData.length];

            for(int i = 0; i < originalData.length; i++) {
                imageData[i] = originalData[i];
            }
        } catch (Exception e) {
            log.info(e.getMessage());

            return new byte[0];
        }

        return imageData;
    }

    public String getExtension(Long imageId) {
        try {
            Image image = this.getById(imageId);

            if (image.getExtension().equals("jpeg") || image.getExtension().equals("jpg")) {
                return "jpg";
            }

            if (image.getExtension().equals("png")) {
                return "png";
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
