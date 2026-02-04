package kz.lab.fileuploaderservice.service.impl;

import kz.lab.fileuploaderservice.db.entity.File;
import kz.lab.fileuploaderservice.db.enums.FileStatus;
import kz.lab.fileuploaderservice.db.repository.FileRepository;
import kz.lab.fileuploaderservice.dto.response.FileInfoResponse;
import kz.lab.fileuploaderservice.dto.response.FileUploadResponse;
import kz.lab.fileuploaderservice.mapper.FileMapper;
import kz.lab.fileuploaderservice.processor.AsyncFileProcessor;
import kz.lab.fileuploaderservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final AsyncFileProcessor asyncFileProcessor;
    private final FileMapper fileMapper;

    @Override
    @Transactional(readOnly = true)
    public FileInfoResponse getFile(Long fileId) {

        File file = fileRepository.findById(fileId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "File not found"
                        )
                );

        return fileMapper.toDTO(file);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileInfoResponse> getAllFiles() {
        return fileRepository.findAll()
                .stream()
                .map(fileMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public FileUploadResponse handleUpload(
            String clientId,
            String idempotencyKey,
            MultipartFile file
    ) {
        return fileRepository
                .findByClientIdAndIdempotencyKey(clientId, idempotencyKey)
                .map(existing ->
                        new FileUploadResponse(
                                existing.getId(),
                                existing.getStatus()
                        )
                )
                .orElseGet(() -> {
                    File entity = File.builder()
                            .clientId(clientId)
                            .idempotencyKey(idempotencyKey)
                            .fileName(file.getOriginalFilename())
                            .status(FileStatus.UPLOADING)
                            .createdAt(LocalDateTime.now())
                            .build();

                    File saved = fileRepository.save(entity);

                    byte[] content;
                    try {
                        content = file.getBytes();
                    } catch (IOException e) {
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Cannot read file",
                                e
                        );
                    }

                    asyncFileProcessor.processAsync(
                            saved.getId(),
                            content,
                            file.getOriginalFilename()
                    );

                    return new FileUploadResponse(
                            saved.getId(),
                            saved.getStatus()
                    );
                });
    }
}

