package com.icia.board.service;

import com.icia.board.dto.BoardDTO;
import com.icia.board.dto.BoardFileDTO;
import com.icia.board.dto.PageDTO;
import com.icia.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;


    public void save(BoardDTO boardDTO) throws IOException {
    /*
      -파일 있음
            1. fileAttached=1, board_table에 저장 후 id값 받아오기
            2. 파일원본 이름 가져오기
            3. 저장용 이름 만들기
            4. 파일 저장용 폴더에 파일 저장 처리
            5. board_file_talbe에 관련 정보 저장

      -파일 없음
            fileAttached=0, 나머지는 기존 방식과 동일

     */
        if(boardDTO.getBoardFile().get(0).isEmpty()) { // get(0) = 0번 인덱스를 의미
            // 파일 없음.
            boardDTO.setFileAttached(0);
            boardRepository.save(boardDTO);
        } else {
            // 파일 있음.
            boardDTO.setFileAttached(1);
            // 게시글 저장 후 id값 활용을 위해 리턴 받음.
            BoardDTO savedBoard = boardRepository.save(boardDTO);
            // 파일이 여러개이기 때문에 반복문으로 파일을 하나씩 꺼내서 저장 처리
            for(MultipartFile boardFile: boardDTO.getBoardFile()) {
                // 파일만 따로 가져오기(단일개수)
//              MultipartFile boardFile = boardDTO.getBoardFile();
                // 파일 이름 가져오기
                String originalFileName = boardFile.getOriginalFilename();
                // 저장용 이름 만들기
                String storedFileName = System.currentTimeMillis() + "-" + originalFileName;
                // BoardFileDTO세팅
                BoardFileDTO boardFileDTO = new BoardFileDTO();
                boardFileDTO.setOriginalFileName(originalFileName);
                boardFileDTO.setStoredFileName(storedFileName);
                boardFileDTO.setBoardId(savedBoard.getId());
                // 파일 저장용 폴더에 파일 저장 처리
                String savePath = "D:\\spring_img\\" + storedFileName;
                boardFile.transferTo(new File(savePath));
                // board_file_table 저장 처리
                boardRepository.saveFile(boardFileDTO);
            }
        }
    }


    public List<BoardDTO> list(){
        return boardRepository.list();
    }

    public BoardDTO detail(Long id){
        return boardRepository.detail(id);
    }


    public void update(BoardDTO boardDTO){
        boardRepository.update(boardDTO);
    }
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }
    public void delete(Long id){
        boardRepository.delete(id);
    }

    public List<BoardFileDTO> findFile(Long id){
        return boardRepository.findFile(id);
    }

    public List<BoardDTO> pagingList(int page) {
        int pageLimit = 3; // 한페이지당 보여줄 글 갯수
        int pagingStart = (page-1) * pageLimit; // 요청한 페이지에 보여줄 첫번째 게시글의 순서
        Map<String, Integer> pagingParams = new HashMap<>();
        pagingParams.put("start", pagingStart);
        pagingParams.put("limit", pageLimit);
        return boardRepository.pagingList(pagingParams);
    }

    public PageDTO pageNumber(int page) {
        int pageLimit = 3; // 한페이지에 보여줄 글 갯수
        int blockLimit = 3; // 하단에 보여줄 페이지 번호 갯수
        // 전체 글 갯수 조회
        int boardCount = boardRepository.boardCount();
        // 전체 페이지 갯수 계산
        int maxPage = (int) (Math.ceil((double)boardCount / pageLimit));
        // 시작 페이지 값 계산(1, 4, 7, 10 ~~)
        int startPage = (((int)(Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        // 마지막 페이지 값 계산(3, 6, 9, 12 ~~)
        int endPage = startPage + blockLimit - 1;
        // 전체 페이지 갯수가 계산한 endPage 보다 작을 때는 endPage 값을 maxPage 값과 같게 세팅
        if (endPage > maxPage) {
            endPage = maxPage;
        }
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMaxPage(maxPage);
        pageDTO.setEndPage(endPage);
        pageDTO.setStartPage(startPage);
        return pageDTO;
    }

    public void sampleData(BoardDTO boardDTO) {
        boardDTO.setFileAttached(0);
        boardRepository.save(boardDTO);
    }

    public List<BoardDTO> searchList(String q, String type, int page) {
        Map<String, Object> searchparam = new HashMap<>();
        searchparam.put("q", q);
        searchparam.put("type", type);

        int pageLimit = 3; // 한페이지당 보여줄 글 갯수
        int pagingStart = (page-1) * pageLimit; // 요청한 페이지에 보여줄 첫번째 게시글의 순서
        searchparam.put("start", pagingStart);
        searchparam.put("limit", pageLimit);

        return boardRepository.searchList(searchparam);
    }


    public PageDTO serachPageNumber(String q, String type, int page) {
        int pageLimit = 3; // 한페이지에 보여줄 글 갯수
        int blockLimit = 3; // 하단에 보여줄 페이지 번호 갯수
        Map<String, String> pagingParams = new HashMap<>();
        pagingParams.put("q", q);
        pagingParams.put("type", type);
        // 검색어 기준 글 갯수 조회
        int boardCount = boardRepository.boardSearchCount(pagingParams);
        // 검색어 기준 페이지 갯수 계산
        int maxPage = (int) (Math.ceil((double)boardCount / pageLimit));
        // 검색어 기준 시작 페이지 값 계산(1, 4, 7, 10 ~~)
        int startPage = (((int)(Math.ceil((double) page / blockLimit))) - 1) * blockLimit + 1;
        // 검색어 기준 마지막 페이지 값 계산(3, 6, 9, 12 ~~)
        int endPage = startPage + blockLimit - 1;
        // 검색어 기준 페이지 갯수가 계산한 endPage 보다 작을 때는 endPage 값을 maxPage 값과 같게 세팅
        if (endPage > maxPage) {
            endPage = maxPage;
        }
        PageDTO pageDTO = new PageDTO();
        pageDTO.setPage(page);
        pageDTO.setMaxPage(maxPage);
        pageDTO.setEndPage(endPage);
        pageDTO.setStartPage(startPage);
        return pageDTO;
    }
}
