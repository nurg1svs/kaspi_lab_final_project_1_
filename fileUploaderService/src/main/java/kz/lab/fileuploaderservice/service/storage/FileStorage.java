package kz.lab.fileuploaderservice.service.storage;

public interface FileStorage {
    String store(byte[] content, String filename) throws Exception;
}
