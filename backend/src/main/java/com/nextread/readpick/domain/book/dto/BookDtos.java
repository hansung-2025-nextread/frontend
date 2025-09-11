package com.nextread.readpick.domain.book.dto;

import java.util.List;

public class BookDtos {

    public record BookCardDto(String id, String title, List<String> authors, String coverUrl) {
    }

    public record BookDetailDto(String id, String title, List<String> authors,
                                String publisher, String pubDate, String summary,
                                String isbn, String coverUrl) {
    }
}
