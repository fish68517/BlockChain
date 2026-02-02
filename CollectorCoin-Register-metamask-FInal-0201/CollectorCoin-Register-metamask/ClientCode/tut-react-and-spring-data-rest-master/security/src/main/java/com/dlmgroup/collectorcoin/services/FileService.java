package com.dlmgroup.collectorcoin.services;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  public void init();

  public void save(MultipartFile file);

  public String saveToUserIdFolder(MultipartFile file, Long userId);

  public Resource load(String filename);

  public void deleteFile(String path);

  public void deleteAll();

  public Stream<Path> loadAll();
}
