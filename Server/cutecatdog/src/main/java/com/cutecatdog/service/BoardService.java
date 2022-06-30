package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDetailDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.board.BoardWhereDto;

import org.springframework.stereotype.Service;
@Service
public interface BoardService {

  public List<BoardResponsDto> findBoard() throws Exception;
 
  public List<BoardDto> findTypeBoard(int typeId) throws Exception;

  public boolean addBoard(BoardAddRequestDto boardAddRequestDto) throws Exception;

  public boolean modifyBoard(BoardModifyRequestDto boardModifyRequestDto) throws Exception;

  public boolean removeBoard(int id) throws Exception;

  public BoardDetailDto findDetailBoard(int id) throws Exception;

  public List<BoardDto> findUserBoard(int userId) throws Exception;

  public List<BoardResponsDto> findDongBoard(BoardWhereDto dto) throws Exception;

  
}
