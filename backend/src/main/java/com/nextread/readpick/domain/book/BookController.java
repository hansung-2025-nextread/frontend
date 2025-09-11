package com.nextread.readpick.domain.book;

import com.nextread.readpick.domain.book.dto.BookDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/bestsellers")
    public ResponseEntity<List<BookDtos.BookCardDto>> bestsellers(
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(bookService.getBestsellers(size));
    }

    @GetMapping("/{id}")
    public BookDtos.BookDetailDto detail(@PathVariable String id) {
        return bookService.getDetail(id);
    }
}
