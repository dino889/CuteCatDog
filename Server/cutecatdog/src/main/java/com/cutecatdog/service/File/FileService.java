package com.cutecatdog.service.File;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  public boolean saveFile(MultipartFile file);

  public boolean deleteFile(MultipartFile file);

  public void downloadAPK(HttpServletResponse res);
}
