package com.jojoldu.book.springboot.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.persistence.*;

/*
* 주요 어노테이션은 클래스 가깝게
* Entity JPA 어노테이션
* Entity 클래스에서는 Setter 메소드를 생성하지 않는데, 인스턴스 값이 어디서 변해야 하는지 코드상으로 명확하지 않기 때문
* Getter, NoArgsConstructor 롬복 어노테이션
* Posts.class는 DB 테이블과 매칭될 Entity 클래스
* JPA를 사용하면 DB에 실제 쿼리를 날리기보다는 Entity 클래스 수정을 통해 작업
* */
@Getter
@NoArgsConstructor
@Entity // 테이블과 링크될 클래스임을 표시, 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 매칭
public class Posts {

    @Id //PK 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK 생성규칙
    private Long id;

    @Column(length = 500, nullable = false) // 테이블 칼럼을 선언 하는데, 필드는 기본적으로 칼럼으로 취급됨 
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(Long id, String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
