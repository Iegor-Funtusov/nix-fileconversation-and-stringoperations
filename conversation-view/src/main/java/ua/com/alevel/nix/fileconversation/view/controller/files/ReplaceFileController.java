package ua.com.alevel.nix.fileconversation.view.controller.files;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ua.com.alevel.nix.fileconversation.view.config.ConversationType;
import ua.com.alevel.nix.fileconversation.view.data.FileData;
import ua.com.alevel.nix.fileconversation.view.exception.StorageFileNotFoundException;
import ua.com.alevel.nix.fileconversation.view.service.StorageFileService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/replace_files")
public class ReplaceFileController extends AbstractFileController {

    private final StorageFileService storageFileService;

    public ReplaceFileController(StorageFileService storageFileService) {
        this.storageFileService = storageFileService;
    }

    @Override
    @GetMapping
    protected String getAllFiles(Model model) {
        model.addAttribute("files", storageFileService.loadAll(ConversationType.REPLACE)
                .map(path -> new FileData(path, ConversationType.REPLACE))
                .collect(Collectors.toList()));
        return "page/files/replace_files";
    }

    @Override
    @GetMapping("/files/{filename:.+}")
    protected ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageFileService.loadAsResource(filename, ConversationType.REPLACE);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @Override
    @GetMapping("/files/delete/{filename}")
    protected String deleteFile(@PathVariable String filename) {
        storageFileService.delete(filename, ConversationType.REPLACE);
        return "redirect:/replace_files";
    }

    @Override
    protected String handleFileUpload(MultipartFile file, ConversationType conversationType, RedirectAttributes redirectAttributes) {
        return null;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
