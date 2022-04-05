package com.cutecatdog.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.cutecatdog.service.File.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/download")
@Api(tags = "download")
public class DownloadController {

  @Autowired
  private FileService fileService;

  @GetMapping()
  public void doDownload(HttpServletResponse res) {
    fileService.downloadAPK(res);
  }
}
