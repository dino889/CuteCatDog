package com.cutecatdog.service;

import java.sql.SQLException;
import java.util.List;

import com.cutecatdog.mapper.BoardMapper;
import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService{

  @Autowired
  private SqlSession sqlSession;

  @Override
  public List<BoardDto> findBoard() throws Exception {
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
  
}
