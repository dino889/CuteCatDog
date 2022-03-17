package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;

public interface BoardMapper {

  public List<BoardDto> selectBoard() throws SQLException;

  public boolean insertBoard(BoardAddRequestDto boardAddRequestDto) throws SQLException;

  public boolean updateBoard(BoardModifyRequestDto boardModifyRequestDto) throws SQLException;

  public boolean deleteBoard(int id) throws SQLException;
  
}
