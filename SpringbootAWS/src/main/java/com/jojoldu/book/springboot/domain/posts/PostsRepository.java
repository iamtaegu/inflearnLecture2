package com.jojoldu.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository 상속하면 기본적인 CRUD 메소드 자동 생성
 * Entity 클래스와 Entity Repository는 함께 위치
 */

public interface PostsRepository  extends JpaRepository<Posts, Long> {

}
