package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.CardDto;
import com.example.BusinessCard.Mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class TextController {
    private static final List<String> POSITION = Arrays.asList(
            "사장", "부사장", "이사", "전무", "상무", "부장", "차장", "과장", "대리", "사원", "대표", "팀장", "매니저", "실장", "간부"
    );
    private static final List<String> DEPARTMENTS = Arrays.asList(
            "영업부", "기획부", "인사부", "총무부", "재무부", "마케팅부", "IT부", "개발부", "디자인팀", "고객지원부", "물류팀",
            "R&D부", "서비스팀", "품질관리부", "구매부", "생산부", "경영지원부", "인재개발부", "전략기획부"
    );
    @Autowired
    private CardMapper cardMapper;

    public static String getUniqueFileName() {
        UUID uuid = UUID.randomUUID();
        String uniqueFileName = uuid.toString();
        return uniqueFileName;
    }

    @PostMapping("/autoUpload")
    public String extractText(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttrs, HttpSession session) {
        processImage(session, file);
        String extractedText = (String) session.getAttribute("extractedText");
        String username = (String) session.getAttribute("username");
        List<CardDto> cardList = cardMapper.getCardList();

        if (username == null) {
            return "login";
        }
        
        if (extractedText == null) {
            return "autoUpload";
        }

        // 리디렉션 속성에 데이터 추가
        redirectAttrs.addFlashAttribute("cardList", cardList);
        redirectAttrs.addFlashAttribute("extractedText", extractedText);
        redirectAttrs.addFlashAttribute("username", username);

        // 리디렉션 추가
        return "redirect:/board";
    }

    private void processImage(HttpSession session, MultipartFile file) {
        String extractedText = "";
        ImageAnnotatorClient client = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024)) {

            Path uploadsDirectory = Paths.get("./uploads");
            if (!Files.exists(uploadsDirectory)) {
                Files.createDirectories(uploadsDirectory);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                System.err.println("Error");
                return;
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = getUniqueFileName() + fileExtension;

            Path filePath = uploadsDirectory.resolve(uniqueFileName);
            BufferedImage image;
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath);
                image = ImageIO.read(filePath.toFile()); // 사용한 스트림을 닫기 위해 파일 경로를 사용하여 이미지를 읽습니다.
            }

            int maxWidth = 800;
            int maxHeight = 600;
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            float aspectRatio = (float) originalWidth / originalHeight;
            int newWidth, newHeight;

            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                if (originalWidth >= originalHeight) {
                    newWidth = maxWidth;
                    newHeight = Math.round(maxWidth / aspectRatio);
                } else {
                    newHeight = maxHeight;
                    newWidth = Math.round(maxHeight * aspectRatio);
                }
            } else {
                newWidth = originalWidth;
                newHeight = originalHeight;
            }

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

            ImageIO.write(resizedImage, "jpg", baos);
            byte[] compressedImageBytes = baos.toByteArray();

            ByteString imgBytes = ByteString.copyFrom(compressedImageBytes);
            com.google.cloud.vision.v1.Image visionImage = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();

            client = ImageAnnotatorClient.create();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            ImageContext imageContext = ImageContext.newBuilder().addLanguageHints("ko").build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(visionImage)
                    .setImageContext(imageContext)
                    .build();
            requests.add(request);

            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse annotateResponse : responses) {
                if (annotateResponse.hasError()) {
                    System.err.println("Error: " + annotateResponse.getError().getMessage());
                    return;
                }
                extractedText = annotateResponse.getTextAnnotations(0).getDescription();
                break;
            }
            System.out.println(extractedText);

            List<String> lines = new ArrayList<>(Arrays.asList(extractedText.split("\n")));

            for (String line : lines) {
                System.out.println(line);
            }

            session.setAttribute("uniqueFileName", uniqueFileName);
            session.setAttribute("extractedText", extractedText);

            autoUpload(session, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (client != null) {
            client.close();
        }
    }

    private void autoUpload(HttpSession session, List<String> lines) {
        String phoneNumber = extractPhoneNumber(lines);
        String name = extractName(lines);
        String username = (String) session.getAttribute("username");
        String uniqueFileName = (String) session.getAttribute("uniqueFileName");
        String email = extractEmail(lines);
        String number = extractNumber(lines);
        String fax = extractFaxNumber(lines);
        String[] departmentAndPosition = extractDepartmentAndPosition(lines);
        String department = departmentAndPosition[0];
        String position = departmentAndPosition[1];
        String address = extractAddress(lines);
        String company = extractCompanyName(lines);

        saveCard(phoneNumber, username, name, uniqueFileName, email, fax, number, department, position, address, company);
    }

    private static String extractName(List<String> lines) {
        Pattern namePattern = Pattern.compile("[가-힣]{2,5}");

        for (String line : lines) {
            Matcher matcher = namePattern.matcher(line);

            if (matcher.find()) {
                return matcher.group();
            }
        }

        return "";
    }

    private String extractEmail(List<String> lines) {
        Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Z|a-z]");

        for (String line : lines) {
            Matcher matcher = emailPattern.matcher(line);

            if (matcher.find()) {
                return matcher.group();
            }
        }

        return "";
    }

    private String extractPhoneNumber(List<String> lines) {
        Pattern phoneNumberPattern = Pattern.compile("(?:0)?10[-\\s]\\d{3,4}[-\\s]\\d{4}");

        for (String line : lines) {
            Matcher matcher = phoneNumberPattern.matcher(line);

            if (matcher.find()) {
                return matcher.group();
            }
        }

        return "";
    }

    private String extractNumber(List<String> lines) {
        Pattern phonePattern = Pattern.compile("\\d{2,3}[-\\s]\\d{3,4}[-\\s]\\d{4}");

        for (String line : lines) {
            if (line.contains("전화") || line.contains("tel") || line.contains("T") || line.contains("office")) {
                Matcher matcher = phonePattern.matcher(line);

                if (matcher.find()) {
                    return matcher.group().replaceAll("\\s+", "");
                }
            }
        }

        return "";
    }

    private String extractFaxNumber(List<String> lines) {
        Pattern faxPattern = Pattern.compile("\\d{2,3}[-\\s]\\d{3,4}[-\\s]\\d{4}");

        for (String line : lines) {
            if (line.contains("팩스") || line.contains("Fax") || line.contains("F")) {
                Matcher matcher = faxPattern.matcher(line);

                if (matcher.find()) {
                    return matcher.group().replaceAll("\\s+", "");
                }
            }
        }

        return "";
    }

    private String[] extractDepartmentAndPosition(List<String> lines) {
        String[] result = new String[2];
        String department = "";
        String position = "";

        for (String line : lines) {
            String[] parts = line.split("\\s*/\\s*");

            if (parts.length == 2) {
                department = parts[0].trim();
                position = parts[1].trim();
                break;
            } else {
                String extractedDepartment = extractDepartment(line);
                String extractedPosition = extractPosition(line);

                if (department.isEmpty() && !extractedDepartment.isEmpty()) {
                    department = extractedDepartment;
                }
                if (position.isEmpty() && !extractedPosition.isEmpty()) {
                    position = extractedPosition;
                }

                if (!department.isEmpty() && !position.isEmpty()) {
                    break;
                }
            }
        }

        result[0] = department;
        result[1] = position;
        return result;
    }

    private String extractPosition(String line) {
        for (String position : POSITION) {
            Pattern pattern = Pattern.compile("\\b" + position + "\\b");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                return position;
            }
        }
        return "";
    }

    private String extractDepartment(String line) {
        for (String department : DEPARTMENTS) {
            Pattern pattern = Pattern.compile("\\b" + department + "\\b");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                return department;
            }
        }
        return "";
    }

    private String extractAddress(List<String> lines) {
        Pattern addressPattern = Pattern.compile("(?:\\b\\d{3}(?:-)?\\d{2,3}\\b\\s*)?([가-힣]+(시|도)\\s*[가-힣]+(시|군|구)(?:\\s*[가-힣]+(읍|면|동|가|리))?\\s*[가-힣]+(로|길|대로)\\s*\\d+(?:-[0-9]{1,4}|)(?:\\s*\\([^)]+\\))?)");

        for (String line : lines) {
            Matcher matcher = addressPattern.matcher(line);

            if (matcher.find()) {
                return matcher.group();
            }
        }

        return "";
    }

    private String extractCompanyName(List<String> lines) {
        Pattern companyNamePattern = Pattern.compile("([가-힣]+(주|유|합|조|계열|자동차|미소|대리점|가스|대행)\\s?[가-힣A-Za-z0-9]+\\s?\\(?(주식사|유한회사|합자회사|조인트벤쳐|Inc|Ltd|LLC|Co|GmbH|AG|S\\.?A\\.?|S\\.?A\\.?R\\.?L\\.?|S\\.?L\\.?)?\\)?)|" +
                "((Inc|Ltd|LLC|Co|GmbH|AG|S\\.?A\\.?|S\\.?A\\.?R\\.?L\\.?|S\\.?L\\.?)\\.?\\s?[A-Za-z0-9]+)");

        for (String line : lines) {
            Matcher matcher = companyNamePattern.matcher(line);

            if (matcher.find()) {
                return matcher.group();
            }
        }

        return "";
    }

    private void saveCard(String phoneNumber, String username, String name, String uniqueFileName, String email, String fax, String number, String department, String position, String address, String company) {
        CardDto newCard = new CardDto();
        newCard.setPhone_number(phoneNumber);
        newCard.setUsername(username);
        newCard.setName(name);
        newCard.setImage(uniqueFileName);
        newCard.setEmail(email);
        newCard.setFax(fax);
        newCard.setNumber(number);
        newCard.setDepartment(department);
        newCard.setPosition(position);
        newCard.setAddress(address);
        newCard.setCompany_name(company);

        cardMapper.autoUpload(newCard);
    }
}


