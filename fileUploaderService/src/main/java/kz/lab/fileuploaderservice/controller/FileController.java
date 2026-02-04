package kz.lab.fileuploaderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.lab.fileuploaderservice.dto.response.FileInfoResponse;
import kz.lab.fileuploaderservice.dto.response.FileUploadResponse;
import kz.lab.fileuploaderservice.service.impl.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileServiceImpl fileService;

    @Operation(summary = "Получить информацию о файле")
    @ApiResponse(responseCode = "200", description = "Файл найден")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    @GetMapping("/{id}")
    public ResponseEntity<FileInfoResponse> getFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFile(id));
    }

    @Operation(summary = "Получить информацию о всех файлах")
    @ApiResponse(responseCode = "200", description = "Файлы найдены")
    @ApiResponse(responseCode = "404", description = "Файлы не найдены")
    @GetMapping
    public ResponseEntity<List<FileInfoResponse>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    @Operation(summary = "Загрузка файла")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Файл принят в обработку"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader("X-Client-Id") String clientId,
            @RequestPart("file") MultipartFile file
    ) {
        FileUploadResponse response =
                fileService.handleUpload(clientId, idempotencyKey, file);

        return ResponseEntity.accepted().body(response);
    }
}

