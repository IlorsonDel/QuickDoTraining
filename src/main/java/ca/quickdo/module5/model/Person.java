package ca.quickdo.module5.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
    private Integer id;
    private String names, phone, email, city, avatar;

}