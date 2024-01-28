package com.dxsys.graphql.springgraphql.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    private int id;
    private String name;
    private  String mobile;
    private String email;
    private String[] address;


}
