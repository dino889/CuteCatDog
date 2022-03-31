package com.cutecatdog.model.pet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetWithKind {
  Integer id;
  Integer userId;
  String kind;
  String name;
  String birth;
  String photoPath;
  Integer gender;
  Integer isNeutered;
}
