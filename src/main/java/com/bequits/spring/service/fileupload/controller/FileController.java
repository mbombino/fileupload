package com.bequits.spring.service.fileupload.controller;

import com.bequits.spring.service.fileupload.message.ResponseMessage;
import com.bequits.spring.service.fileupload.model.FileInfo;
import com.bequits.spring.service.fileupload.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import java.util.List;

@Controller
@CrossOrigin("http://localhost:8081")
public class FileController {
    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage>uploadFile(@RequestParam(value = "file") MultipartFile file){
        String message ="";
        try {
            fileStorageService.save(file);
            message = "File upload successful: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        }catch (Exception e){
            message="Could not upload file: " + file.getOriginalFilename() + ". Error: "+e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>>getFileList(){
        List<FileInfo> fileInfo = fileStorageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder.fromMethodName(FileController.class,"getFile",
                    path.getFileName().toString()).build().toString();
            return new FileInfo(filename,url);
        }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(fileInfo);
    }
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource>getFile(@PathVariable String filename){
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""
        +file.getFilename()+"\"").body(file);
    }
    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<ResponseMessage>deleteFile(@PathVariable String filename){
        String message="";
        try {
            boolean fileExists= fileStorageService.delete(filename);
            if(fileExists){
                message="File removal successful: "+filename;
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            }
            message="File does not exist";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
        }catch (Exception e){
            message="Could not removal the file: "+filename+". Error: "+e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
        }
    }

}
