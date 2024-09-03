package com.bequits.spring.service.fileupload.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path root = Paths.get("uploads");
    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        }catch (IOException e){
            throw new RuntimeException("Could not initialise folder for uploads");

        }

    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(),root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (Exception e) {
            if(e instanceof FileAlreadyExistsException){
                throw new RuntimeException("A file of that name already exists");
            }
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Resource load(String filename) {
        try {
            Path file =root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists()|| resource.isReadable()){
                return resource;
            }else {
                throw new RuntimeException("Could not read file");
            }

        }catch (MalformedURLException e){
            throw new RuntimeException("Error: "+e.getMessage());

        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());

    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = root.resolve(filename);
            return Files.deleteIfExists(file);
        }catch (IOException e){
            throw new RuntimeException("Error: "+e.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            final Stream<Path> pathStream;
            pathStream = walk(root, 1).filter(path -> !path.equals(root)).map(root::relativize);
            return pathStream;
        }catch (IOException e){
            throw new RuntimeException("Could not load files");

        }
    }
}
