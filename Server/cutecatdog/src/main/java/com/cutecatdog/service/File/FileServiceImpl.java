package com.cutecatdog.service.File;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

  @Value("${file.dir}")
  private String targetDir;

  @Value("${file.apk}")
  private String apkPath;

  @Override
  public boolean saveFile(MultipartFile file) {
    Path path = Paths.get(targetDir + file.getOriginalFilename());
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
    Path path = Paths.get(targetDir + file.getOriginalFilename());
    try {
      Files.delete(path);
      return true;
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return false;
    }
  }

  @Override
  public void downloadAPK(HttpServletResponse res) {
    try {
      File file = new File(apkPath);
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
      res.setContentType("application/octet-stream");
      res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
      FileCopyUtils.copy(bis, res.getOutputStream());
      bis.close();
      res.getOutputStream().flush();
      res.getOutputStream().close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

  }
}
