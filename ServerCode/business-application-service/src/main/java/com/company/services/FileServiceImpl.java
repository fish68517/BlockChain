package com.company.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {
  private final Path root = Paths.get("uploads");

  @Override
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  @Override
  public void save(MultipartFile file) {
    try {
      Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  @Override
  public String saveToUserIdFolder(MultipartFile file, Long userId) {
    try {
      Path userFolder = root.resolve(userId.toString());
      Files.createDirectories(userFolder);

      String filename = StringUtils.cleanPath(file.getOriginalFilename());
      Path targetLocation = userFolder.resolve(filename);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return targetLocation.toString();
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  @Override
  public Resource load(String filename) {
    try {
      Path file = root.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteFile(String path) {
    try {
      Files.deleteIfExists(Paths.get(path));
    } catch (IOException e) {
      throw new RuntimeException("Could not delete the file. Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    try {
      Files.walk(root)
          .filter(Files::isRegularFile)
          .map(Path::toFile)
          .forEach(java.io.File::delete);
    } catch (IOException e) {
      throw new RuntimeException("Could not delete all files. Error: " + e.getMessage());
    }
  }

  @Override
  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.root, 1)
          .filter(path -> !path.equals(this.root))
          .map(this.root::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load the files!");
    }
  }
}