package com.cutecatdog.model.pet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetDto {
  Integer id;
  Integer userId;
  Integer kindId;
  String name;
  String birth;
  String photoPath;
  Integer gender;
  Integer isNeutered;
}
