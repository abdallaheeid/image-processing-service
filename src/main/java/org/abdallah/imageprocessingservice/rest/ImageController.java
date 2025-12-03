package org.abdallah.imageprocessingservice.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.abdallah.imageprocessingservice.dto.ImageResponse;
import org.abdallah.imageprocessingservice.dto.TransformRequest;
import org.abdallah.imageprocessingservice.image.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Images", description = "Image upload, listing, retrieval and transformation")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Upload an image", description = "Uploads an image and returns its metadata.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded", content = @Content(
                    schema = @Schema(implementation = ImageResponse.class)
            ))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull ImageResponse> upload(@RequestPart("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(imageService.upload(file));
    }

    @Operation(summary = "Retrieve an image file", description = "Returns the actual binary image.")
    @GetMapping("/{uuid}")
    public ResponseEntity<@NonNull Resource> getImage(@PathVariable String uuid) {
        Resource resource = imageService.loadImageResource(uuid);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "List images", description = "Returns paginated list of images for current user.")
    @GetMapping
    public ResponseEntity<@NonNull Page<@NonNull ImageResponse>> listImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pr = PageRequest.of(page, limit);
        return ResponseEntity.ok(imageService.listImages(pr));
    }

    @Operation(summary = "Transform an image", description = "Apply transformations like resize, crop, rotate, filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transformation applied", content = @Content(
                    schema = @Schema(implementation = ImageResponse.class)
            ))
    })
    @PostMapping("/{uuid}/transform")
    public ResponseEntity<@NonNull ImageResponse> transform(
            @PathVariable String uuid,
            @RequestBody(
                    description = "Transformation instructions",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransformRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody
            TransformRequest request
    ) throws Exception {
        return ResponseEntity.ok(imageService.transform(uuid, request));
    }
}
