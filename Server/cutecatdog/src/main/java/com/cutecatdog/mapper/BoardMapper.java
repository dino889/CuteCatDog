package com.cutecatdog.mapper;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDetailDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.board.BoardWhereDto;
import com.cutecatdog.model.comment.commentDto;
import com.cutecatdog.model.like.LikeUserResponsDto;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

  public List<BoardResponsDto> selectBoard() throws SQLException;
  
  public List<BoardDto> selectTypeBoard(int typeId) throws SQLException;

  public boolean insertBoard(BoardAddRequestDto boardAddRequestDto) throws SQLException;

  public boolean updateBoard(BoardModifyRequestDto boardModifyRequestDto) throws SQLException;

  public boolean deleteBoard(int id) throws SQLException;

  public BoardDetailDto selectDetailBoard(int id) throws SQLException;

  public List<commentDto> selectComment(int id) throws SQLException;

  public List<LikeUserResponsDto> selectLike(int id) throws SQLException;

  public List<BoardDto> selectUserBoard(int userId) throws SQLException;

  public List<BoardResponsDto> selectDongBoard(BoardWhereDto dto) throws SQLException;
  
}
