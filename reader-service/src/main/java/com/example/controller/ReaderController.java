package com.example.controller;

import com.example.dto.ReaderRequest;
import com.example.dto.ReaderResponse;
import com.example.model.Token;
import com.example.service.ReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readers")
public class ReaderController {

    private final ReaderService readerService;

    public ReaderController(ReaderService readerService){
        this.readerService = readerService;
    }

    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createReader(@RequestBody ReaderRequest readerRequest) {
        readerService.createReader(readerRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReaderResponse> getReaders(@RequestBody Token token) {
        return readerService.getAllReadersByUser(token.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReaderResponse> getAllReaders(){
        return readerService.getAllReaders();
    }
}

