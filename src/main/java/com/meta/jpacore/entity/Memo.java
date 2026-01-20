package com.meta.jpacore.entity;


import jakarta.persistence.*;

@Entity // JPA가 관리 할 수 있는 Entity 클래스로 지정
//@Table(name = "memo") // Default가 클래스 이름
public class Memo {
  @Id //Primary key 설정 (식별자 역할 index)
  @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 기능
  private Long id;

  //nullable : NUll 허용 여부
  //unique : 중복 허용 여부
  @Column(name = "username", nullable = false, unique = true)
  private String username;

  // length : 컬럼 사이즈 지정
  @Column (name = "contents", nullable = false, length = 500)
  private String contents;
}
