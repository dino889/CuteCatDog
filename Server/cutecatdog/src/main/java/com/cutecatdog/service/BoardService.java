package com.cutecatdog.service;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;

public interface BoardService {

  public List<BoardDto> findBoard() throws Exception;

  public boolean addBoard(BoardAddRequestDto boardAddRequestDto) throws Exception;

  public boolean modifyBoard(BoardModifyRequestDto boardModifyRequestDto) throws Exception;

  public boolean removeBoard(int id) throws Exception;
  
}
