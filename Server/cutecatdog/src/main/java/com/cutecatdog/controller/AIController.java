package com.cutecatdog.controller;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.AI.AIResponseDto;
import com.cutecatdog.service.AI.AIService;
import com.cutecatdog.service.File.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/ai")
@Api(tags = "ai")
public class AIController {
  @Autowired
  private AIService aiService;

  @Autowired
  private FileService fileService;

  @PostMapping()
  public ResponseEntity<Message> getAnimalKind(@RequestParam("file") MultipartFile file) {
    Message msg = new Message();
    HttpStatus status = HttpStatus.OK;

    AIResponseDto aiResponse = new AIResponseDto();
    msg.setSuccess(true);

    if (fileService.saveFile(file)) {
      aiResponse.setSuccess(true);
      aiResponse.setKind(aiService.getAnimalKind(file.getOriginalFilename()));
      fileService.deleteFile(file);
    } else {
      msg.setMessage("이미지 저장 실패");
      msg.setData(aiResponse);
    }

    return new ResponseEntity<Message>(msg, status);
  }

  @GetMapping("/test")
  public ResponseEntity<Message> getAnimalKind() {
    Message msg = new Message();
    HttpStatus status = HttpStatus.OK;

    AIResponseDto aiResponse = new AIResponseDto();
    msg.setSuccess(true);

    aiResponse.setKind(aiService.getAnimalKind("KakaoTalk_20220401_144628666.jpg"));

    return new ResponseEntity<Message>(msg, status);
  }
}
