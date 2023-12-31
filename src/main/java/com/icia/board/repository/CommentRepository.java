package com.icia.board.repository;

import com.icia.board.dto.CommentDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {
    @Autowired
    private SqlSessionTemplate sql;

    public void save(CommentDTO commentDTO){
//        System.out.println("commentDTO = " + commentDTO);
        sql.insert("Comment.save", commentDTO);
    }

    public CommentDTO findById(CommentDTO commentDTO){
        return sql.selectOne("Comment.findById", commentDTO);
    }

    public List<CommentDTO> findAll(Long boardId) {
        return sql.selectList("Comment.findAll", boardId);
    }
}
