package com.cutecatdog.service.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

  @Value("${file.dir}")
  private String targetDir;

  @Override
  public boolean saveFile(MultipartFile file) {
    Path path = Paths.get("targetDir/" + file.getOriginalFilename());
    try {
      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("저장 성공");
      return true;
    } catch (IOException e) {
      System.out.println("저장 실패 : " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean deleteFile(MultipartFile file) {
    Path path = Paths.get("targetDir/" + file.getOriginalFilename());
    try {
      Files.delete(path);
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

}
