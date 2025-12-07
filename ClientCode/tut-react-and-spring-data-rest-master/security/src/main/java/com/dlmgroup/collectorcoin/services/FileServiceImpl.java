package com.dlmgroup.collectorcoin.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

  private final Path root = Paths.get("target/classes/static/assets/uploads/");

  @Value("${collectorcoin.app.absoluteUploadPath}")
  private String targetPath;

  public void init() {
    Path targetFolder = Paths.get(targetPath);
    Path rootAbsolute = root.toAbsolutePath();

    System.out.println("Creating sym link from " + rootAbsolute.toString() + " to " + targetFolder.toString());

    try {
      Files.createSymbolicLink(rootAbsolute, targetFolder);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void save(MultipartFile file) {
    Path targetFolder = Paths.get(targetPath);

    try {
      Files.copy(file.getInputStream(), targetFolder.resolve(file.getOriginalFilename()));
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
    }
  }

  // returns file path relative to /target/classes/static
  public String getRelativePath(String path) {
    int index = path.indexOf("uploads");

    return path.substring(index + 7);
  }

  public String saveToUserIdFolder(MultipartFile file, Long userId) {
    Path targetFolder = Paths.get(targetPath);
    Path userPath = targetFolder.resolve(userId.toString());
    System.out.println("New path " + userPath);
    try {
      Files.createDirectories(userPath);
    } catch(IOException e) {
      throw new RuntimeException("Could not create a subdirectory: " + e.getMessage());
    }

    Path fileLocation = userPath.resolve(file.getOriginalFilename());

    try {
      Files.copy(file.getInputStream(), fileLocation);
    } catch (Exception e) {
      throw new RuntimeException("Could not store the file. Error: " + e.getClass() + " " + e.getMessage());
    }

    System.out.println("Saved file " + fileLocation);
    return getRelativePath(fileLocation.toString());
  }

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

  public void deleteAll() {
    FileSystemUtils.deleteRecursively(root.toFile());
  }

  public void deleteFile(String path) {
    Path rootAbsolute = root.toAbsolutePath();
    Path absoluteFileLocation = Paths.get(rootAbsolute.toString() + path);
    Path outsideFileLocation = Paths.get(targetPath + path);

    try {
      Files.delete(absoluteFileLocation);
      // Files.delete(outsideFileLocation);
    } catch (IOException e) {
      throw new RuntimeException("Could not delete file. Error: " + e.getClass() + " " + e.getMessage());
    }
  }

  public Stream<Path> loadAll() {
    try {
      return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load the files!");
    }
  }
}
