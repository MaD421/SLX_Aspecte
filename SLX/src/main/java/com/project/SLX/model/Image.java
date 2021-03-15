package com.project.SLX.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "image")
@Entity(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long imageId;

    @NotBlank
    @Column(nullable = false)
    private String extension;

    @Lob
    @NotEmpty
    @Column(nullable = false)
    private Byte[] image;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "listingId")
    private Listing owner;

    @PreRemove
    public void preRemove() {
        this.getOwner().removeImage(this);
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Byte[] getImage() {
        return image;
    }

    public void setImage(Byte[] image) {
        this.image = image;
    }

    public Listing getOwner() {
        return owner;
    }

    public void setOwner(Listing owner) {
        this.owner = owner;
    }
}
