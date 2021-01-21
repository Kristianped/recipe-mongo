package no.kristianped.recipemongo.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface ImageService {

    Mono<Void> saveImageFile(String id, FilePart file);
}
