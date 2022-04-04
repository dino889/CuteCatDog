package com.cutecatdog.service.AI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;

public class AIServiceImpl implements AIService {

  @Value("${file.dir}")
  private String targetDir;

  @Override
  public String getAnimalKind(String fileName) {
    Process process;
    String line = "";
    try {
      process = Runtime.getRuntime()
          .exec(String.format("sudo docker exec ai sh -c \"python /ai/prediction.py /image/\"", fileName));

      StringBuilder sb = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return line;
  }
}
