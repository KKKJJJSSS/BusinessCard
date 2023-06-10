package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.CardDto;
import com.example.BusinessCard.Mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardMapper cardMapper;

    @PostMapping("/upload")
    public Map<String, String> upload(HttpSession session, MultipartHttpServletRequest request) throws Exception {
        Map<String, String> response = new HashMap<>();
        String username = (String) session.getAttribute("username");
        String name = request.getParameter("name");
        MultipartFile image = request.getFile("image");

        if (StringUtils.isEmpty(name)) {
            response.put("result", "이름을 입력하세요.");
        } else {
            CardDto card = new CardDto();
            card.setUsername(username);
            card.setName(name);
            card.setDepartment(request.getParameter("department") != null ? request.getParameter("department") : "");
            card.setPosition(request.getParameter("position") != null ? request.getParameter("position") : "");
            card.setCompany_name(request.getParameter("company_name") != null ? request.getParameter("company_name") : "");
            card.setEmail(request.getParameter("email") != null ? request.getParameter("email") : "");
            card.setPhone_number(request.getParameter("phone_number") != null ? request.getParameter("phone_number") : "");
            card.setAddress(request.getParameter("address") != null ? request.getParameter("address") : "");
            card.setNumber(request.getParameter("number") != null ? request.getParameter("number") : "");
            card.setFax(request.getParameter("fax") != null ? request.getParameter("fax") : "");
            card.setMemo(request.getParameter("memo") != null ? request.getParameter("memo") : "");

            if (image != null && !StringUtils.isEmpty(image.getOriginalFilename())) {
                byte[] bytes = image.getBytes();
                String originalFilename = image.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename.lastIndexOf(".") != -1) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;

                // uploads 디렉토리 생성 및 경로 설정 (루트 디렉터리가 아닌, 현재 디렉터리에 uploads 폴더를 생성합니다.)
                String uploadsPath = new File("./uploads").getCanonicalPath();
                File uploadsDir = new File(uploadsPath);

                if (!uploadsDir.exists()) {
                    uploadsDir.mkdirs();
                }

                // 파일 저장 경로 설정
                Path path = Paths.get(uploadsDir.getAbsolutePath(), fileName);
                Files.write(path, bytes);

                card.setImage(fileName);
            } else {
                card.setImage(""); // 이미지가 없을 경우 빈 문자열로 설정
            }

            cardMapper.insertCard(card);

            response.put("result", "success");
        }

        return response;
    }

    @PostMapping("/reupload")
    public Map<String, String> reupload(MultipartHttpServletRequest request) throws Exception {
        Map<String, String> response = new HashMap<>();
        String name = request.getParameter("name");
        MultipartFile image = request.getFile("image");
        int cardId = Integer.parseInt(request.getParameter("id"));

        // 기존 카드 정보 가져오기
        CardDto existingCard = cardMapper.getCardById(cardId);

        if (StringUtils.isEmpty(name)) {
            response.put("result", "이름을 입력하세요.");
        } else {
            CardDto card = new CardDto();
            card.setName(name);
            card.setDepartment(request.getParameter("department") != null ? request.getParameter("department") : existingCard.getDepartment());
            card.setPosition(request.getParameter("position") != null ? request.getParameter("position") : existingCard.getPosition());
            card.setCompany_name(request.getParameter("company_name") != null ? request.getParameter("company_name") : existingCard.getCompany_name());
            card.setEmail(request.getParameter("email") != null ? request.getParameter("email") : existingCard.getEmail());
            card.setPhone_number(request.getParameter("phone_number") != null ? request.getParameter("phone_number") : existingCard.getPhone_number());
            card.setAddress(request.getParameter("address") != null ? request.getParameter("address") : existingCard.getAddress());
            card.setNumber(request.getParameter("number") != null ? request.getParameter("number") : existingCard.getNumber());
            card.setFax(request.getParameter("fax") != null ? request.getParameter("fax") : existingCard.getFax());
            card.setMemo(request.getParameter("memo") != null ? request.getParameter("memo") : existingCard.getMemo());
            card.setId(cardId);

            if (image != null && !StringUtils.isEmpty(image.getOriginalFilename())) {
                // 기존 이미지 경로를 가져옵니다.
                String oldImagePath = cardMapper.getImagePathById(cardId);

                // 기존 이미지가 있다면 삭제합니다.
                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    Path oldPath = Paths.get("./uploads/" + oldImagePath);
                    Files.deleteIfExists(oldPath);
                }

                byte[] bytes = image.getBytes();
                String originalFilename = image.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename.lastIndexOf(".") != -1) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + fileExtension;

                // uploads 디렉토리 생성 및 경로 설정 (루트 디렉터리가 아닌, 현재 디렉터리에 uploads 폴더를 생성합니다.)
                String uploadsPath = "./uploads";
                File uploadsDir = new File(uploadsPath);

                // 파일 저장 경로 설정
                Path path = Paths.get(uploadsDir.getAbsolutePath() + "/" + fileName);
                Files.write(path, bytes);

                card.setImage(fileName);
            } else {
                // 새 이미지가 업로드되지 않은 경우 기존 이미지를 사용합니다.
                String oldImagePath = cardMapper.getImagePathById(cardId);
                card.setImage(oldImagePath);
            }

            cardMapper.updateCard(card);
            response.put("result", "success");
        }

        return response;
    }

    @DeleteMapping("/delete/{id}")
    public CompletableFuture<ResponseEntity<?>> deleteCard(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                cardMapper.deleteCard(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        });
    }

    @DeleteMapping("/file/delete")
    public ResponseEntity<?> deleteImageFile(@RequestParam("imagePath") String imagePath) {
        try {
            String uploadsPath = "./uploads";
            File uploadsDir = new File(uploadsPath, imagePath);

            if (uploadsDir.exists() && uploadsDir.delete()) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}



