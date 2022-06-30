package com.cutecatdog.service;

import java.util.List;

import com.cutecatdog.mapper.BoardMapper;
import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDetailDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.board.BoardWhereDto;
import com.cutecatdog.model.comment.commentDto;
import com.cutecatdog.model.like.LikeUserResponsDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public List<BoardResponsDto> findBoard() throws Exception {
    return sqlSession.getMapper(BoardMapper.class).selectBoard();
  }

  @Override
  public boolean addBoard(BoardAddRequestDto boardAddRequestDto) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).insertBoard(boardAddRequestDto);
  }

  @Override
  public boolean modifyBoard(BoardModifyRequestDto boardModifyRequestDto) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).updateBoard(boardModifyRequestDto);
  }

  @Override
  public boolean removeBoard(int id) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).deleteBoard(id);
  }

  @Override
  public BoardDetailDto findDetailBoard(int id) throws Exception {
    List<LikeUserResponsDto> likes = sqlSession.getMapper(BoardMapper.class).selectLike(id);
    List<commentDto> list= sqlSession.getMapper(BoardMapper.class).selectComment(id);
    BoardDetailDto data = sqlSession.getMapper(BoardMapper.class).selectDetailBoard(id);
    data.setCount(likes.size());
    data.setCommentDto(list);
    return data;
    
  }

  @Override
  public List<BoardDto> findTypeBoard(int typeId) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).selectTypeBoard(typeId);
  }

  @Override
  public List<BoardDto> findUserBoard(int userId) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).selectUserBoard(userId);
  }

  @Override
  public List<BoardResponsDto> findDongBoard(BoardWhereDto dto) throws Exception {
    return sqlSession.getMapper(BoardMapper.class).selectDongBoard(dto);
  }  
}
