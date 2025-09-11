package com.nextread.readpick.domain.book;

import com.nextread.readpick.domain.book.dto.BookDtos;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class BookService {

    @Cacheable(cacheNames = "bestsellers", key = "#size")
    public List<BookDtos.BookCardDto> getBestsellers(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> new BookDtos.BookCardDto(
                        "MOCK-" + i,
                        "목업 베스트셀러 " + i,
                        List.of("저자" + i),
                        "https://picsum.photos/seed/" + i + "/200/300"
                ))
                .toList();
    }

    public BookDtos.BookDetailDto getDetail(String id) {
        return new BookDtos.BookDetailDto(
                id,
                "목업 상세 제목",
                List.of("홍길동"),
                "가나출판사",
                LocalDate.now().toString(),
                "줄거리 요약...",
                "9781234567890",
                "https://picsum.photos/seed/detail/400/600"
        );
    }
}