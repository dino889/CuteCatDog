package com.cutecatdog.service.AI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIServiceImpl implements AIService {

  @Value("${file.dir}")
  private String targetDir;

  @Override
  public String getAnimalKind(String fileName) {
    Process process;
    String line = "";
    StringBuilder sb = new StringBuilder();
    try {
      String[] cmd = { "/bin/bash", "-c",
          String.format("sudo -S docker exec ai sh -c \"python /ai/prediction.py /image/\"%s", fileName) };
      // process = Runtime.getRuntime()
      // .exec(String.format("sudo docker exec ai sh -c \"python /ai/prediction.py
      // /image/\"", fileName));
      process = Runtime.getRuntime()
          .exec(cmd);

      BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      System.out.println("## RESULT : " + sb.toString());
    } catch (IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

    return sb.toString();
  }
}
