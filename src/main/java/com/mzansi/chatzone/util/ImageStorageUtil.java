package com.mzansi.chatzone.util;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * Saves a captured photo ({@link Part} from a multipart upload) to the
 * application's {@code uploads/} directory and returns the path to store
 * on the message row. Kept deliberately simple — local disk under the
 * deployed webapp — which is the right call for a coursework deployment;
 * a real production rollout would swap this one class for an object-store
 * client (e.g. S3) without touching any caller.
 */
public final class ImageStorageUtil {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_BYTES = 8L * 1024 * 1024; // 8 MB

    private ImageStorageUtil() {
    }

    /**
     * @param uploadPart      the multipart file field from the request
     * @param uploadsRealPath the server filesystem path of the webapp's
     *                        {@code uploads/} directory, from
     *                        {@code ServletContext.getRealPath("/uploads")}
     * @return the path to store in {@code messages.image_path}, relative
     *         to the webapp root (e.g. {@code uploads/3f2a-photo.jpg})
     */
    public static String store(Part uploadPart, String uploadsRealPath) throws IOException {
        String contentType = uploadPart.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }
        if (uploadPart.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Image exceeds the 8MB limit");
        }

        String extension = contentType.equals("image/png") ? ".png"
                : contentType.equals("image/webp") ? ".webp" : ".jpg";
        String fileName = UUID.randomUUID() + extension;

        Path targetDir = Paths.get(uploadsRealPath);
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(fileName);

        try (InputStream in = uploadPart.getInputStream();
             OutputStream out = Files.newOutputStream(targetFile)) {
            in.transferTo(out);
        }

        return "uploads/" + fileName;
    }
}
