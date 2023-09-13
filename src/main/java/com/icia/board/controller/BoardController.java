package com.icia.board.controller;

import com.icia.board.dto.BoardDTO;
import com.icia.board.dto.BoardFileDTO;
import com.icia.board.dto.CommentDTO;
import com.icia.board.dto.PageDTO;
import com.icia.board.service.BoardService;
//import jdk.internal.icu.text.NormalizerBase;
import com.icia.board.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired // 어노테인션은 바로 밑에줄만 적용.
    private BoardService boardService;
    @Autowired
    private CommentService commentService;






    @GetMapping("/save")
    public String save() {
        return "boardSave";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        boardService.save(boardDTO);
        return "redirect:/board/list";
    }

    // /board?id=1
    // /board/list?page=1
    @GetMapping("/list")
    public String list(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                       Model model){
        List<BoardDTO> boardDTOList = boardService.pagingList(page);
        model.addAttribute("boardList", boardDTOList);

        PageDTO pageDTO = boardService.pageNumber(page);
        System.out.println("pageDTO = " + pageDTO);
        model.addAttribute("paging", pageDTO);
        return "boardList";
    }

    @GetMapping("/search")
    public String search (@RequestParam("q") String q,
                          @RequestParam("type") String type,
                          Model model){
        List<BoardDTO> boardDTOList = boardService.searchList(q,type);
        model.addAttribute("boardList", boardDTOList);
        return "boardList";
    }
    @GetMapping("/sample")
    public String sampleData() {
        for (int i = 1; i <= 20; i++) {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setBoardWriter("aa");
            boardDTO.setBoardTitle("title" + i);
            boardDTO.setBoardContents("contents" + i);
            boardDTO.setBoardPass("pass" + i);
            boardService.sampleData(boardDTO);
        }
        return "redirect:/board/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id")Long id,@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                         Model model){
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.detail(id);
        model.addAttribute("board", boardDTO);

        // 첨부된 파일이 있다면 파일을 가져옴
        if (boardDTO.getFileAttached()==1){
            List<BoardFileDTO> boardFileDTOList = boardService.findFile(id);
            model.addAttribute("boardFileList", boardFileDTOList);
        }

        List<CommentDTO> commentDTOList = commentService.findAll(id);
        if (commentDTOList.size() == 0) {
            model.addAttribute("commentList", null);
        }else{
            model.addAttribute("commentList", commentDTOList);
        }
        model.addAttribute("page", page);
        return "boardDetail";
    }



    @GetMapping("/update")
    public String update(@RequestParam("id")Long id, Model model){
        BoardDTO boardDTO = boardService.detail(id);
        model.addAttribute("board", boardDTO);
        return "boardUpdate";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model){
        boardService.update(boardDTO);
        BoardDTO boardDTO1 = boardService.detail(boardDTO.getId());
        model.addAttribute("board", boardDTO1);
        return "boardDetail";
    }

    @GetMapping("/check")
    public String check(@RequestParam("id")Long id, Model model){
        BoardDTO boardDTO = boardService.detail(id);
        model.addAttribute("board", boardService.detail(boardDTO.getId()));
        return "deleteCheck";
    }

    @PostMapping("/check")
    public String check(@RequestParam("id")Long id){
        boardService.delete(id);
        return "redirect:/board/list";
    }
}
