package ru.chaika.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class Product {
    Long id;
    String title;
    Integer price;
    String categoryTitle;
}
