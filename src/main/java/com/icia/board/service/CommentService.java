package com.icia.board.service;

import com.icia.board.dto.CommentDTO;
import com.icia.board.repository.BoardRepository;
import com.icia.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;


    public void save(CommentDTO commentDTO){
//        System.out.println("commentDTO = " + commentDTO);
        commentRepository.save(commentDTO);
    }

    public CommentDTO findById(CommentDTO commentDTO){
       return commentRepository.findById(commentDTO);
    }

    public List<CommentDTO> findAll(Long boardId) {
        return commentRepository.findAll(boardId);
    }
}
