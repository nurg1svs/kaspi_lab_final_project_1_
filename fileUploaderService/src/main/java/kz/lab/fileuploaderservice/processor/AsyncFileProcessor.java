package kz.lab.fileuploaderservice.processor;

import kz.lab.fileuploaderservice.db.enums.FileStatus;
import kz.lab.fileuploaderservice.db.repository.FileRepository;
import kz.lab.fileuploaderservice.service.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncFileProcessor {

    private final FileRepository fileRepository;
    private final FileStorage storage;

    @Async
    public void processAsync(Long fileId, byte[] content, String filename) {
        log.info("Async processing started, fileId={}", fileId);

        try {
            String path = storage.store(content, filename);

            fileRepository.findById(fileId).ifPresent(entity -> {
                entity.setStatus(FileStatus.STORED);
                entity.setStoragePath(path);
                fileRepository.save(entity);
            });

            log.info("DB updated to STORED, fileId={}", fileId);
        } catch (Exception e) {
            log.error("MinIO upload failed, fileId={}", fileId, e);

            fileRepository.findById(fileId).ifPresent(entity -> {
                entity.setStatus(FileStatus.FAILED);
                fileRepository.save(entity);
            });
        }
    }
}

