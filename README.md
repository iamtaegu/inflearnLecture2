[2022-02-08] 무중단 서비스를 위한 AWS 서버 구성 START 

**[EC2 서버구성]**
1. EIP할당 - 고정IP 할당
		> 탄력적IP는 EC2에 연결하지 않으면 비용이 발생하므로 주의
		
1. window > putty 접속
	1. puttygen.exe로 aws에서 받은 pem > ppk로 변환
	1. putty.exe 접속
		1. Session, Host Name 입력
		1. Connection > Auth, ppk 등록
		1. Session, Saved Sessions & 접속 
		
1. 서버 생성 후 필요한 설정들
	1. java설치, sudo yum install -y java-1.8.0-openjdk-devel.x86_64
		1. java 버전 변경, sudo /usr/sbin/alternatives --config java
		1. 미사용 java 삭제, sudo yum remove java-1.7.0-openjdk
	1. 타임존 변경, sudo rm /etc/localtime && sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime
	1. Hostname 변경, sudo vim /etc/sysconfig/network && sudo reboot
		
		
**[RDS 생성 및 설정]**

**[개발 순서]**
1. 서비스, 리포지토리 계층을 개발
2. 테스트 케이스 작성 및 검증
3. 웹 계층 적용 

**[개념]**
* 도메인 모델 패턴 - 엔티티가 비즈니스 로직을 가지고 객체 지향의 특성을 적극 활용
* 트랜잭션 스크립트 패턴 - 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 비즈니스 로직을 처리

**[테스트 케이스]**
* 테스트 케이스는 격리된 환경에서 실행하고, 끝나면 데이터를 초기화하는 것이 좋고
  * 이런 면에서 메모리 DB를 사용하는 것이 이상적
  * 스프링 부트는 datasource 설정이 없으면, 기본적으로 메모리 DB 사용하고, driver-class도 등록된 라이브러리 기준으로 맞춰줌
* 테스트 케이스를 위한 스프링 환경과 실행 환경은 보통 다르므로 설정 파일을 분리하는게 좋음 

**[웹 계층]**
* 요구사항이 단순할 때는 폼 객체(Dto) 없이 엔티티(Domain)를 사용해도 되긴 함
  * 요구사항이 복잡해지면 엔티티에서 화면 처리를 위한 부가 기능이 추가되기 때문에 Dto로 분리
  * 실무에서 엔티티는 핵심 비즈니스 로직만 가지고, 화면을 위한 로직은 없어야 함 


**[변경 감지와 병합(merge)]**
* 준영속 엔티티란 영속성 컨텍스트가 더는 관리하지 않는 엔티티를 말함
  * itemService.saveItem(book)에서 수정을 시도하는 Book 객체인데
  * Book 객체는 이미 DB에 한 번 저장돼 식별자가 존재
  * 엔티티가 기존 식별자를 가지고 있으면 준영속 엔티티로 봄
* 준영속 엔티티 수정 2가지 방법
  * 변경 감지 기능 
    * 영속성 컨텍스트에서 엔티티를 다시 조회한 후 데이터를 수정
  * 병합 기능 
    * 준영속 상태의 엔티티를 영속 상태로 변경
* 엔티티를 변경할 때 가장 좋은 방법
  * 컨트롤러에서 엔티티를 생성하지 말고
  * 트랜잭션이 있는 서비스 계층에 식별자와 변경할 데이터를 명확하게 전달하고
  * 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경하여
  * 트랜잭션 커밋 시점에 변경 감지가 실행되게 한다 

**[API 개발 고급 - JPA]**

OrderSimpleApiController (OneToOne, ManyToOne)
 * 엔티티를 DTO로 변환하거나(V3), DTO로 바로 조회하는 방법(V4)은 각각 장단점이 있음
   * 엔티티로 조회하면 리포지토리 재사용성이 좋고, 개발도 단순
   
 * 쿼리 방식 선택 권장 순서
   1. 우선 엔티티를 DTO로 변환하는 방법으로 진행(V2)
   2. 필요하면 폐치 조인으로 성능 최적화(N+1 문제 해결)
   3. 그래도 안되면 DTO로 직접 조회하는 방법 사용 
   4. JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용
   
OrderApiController (OneToMany)
 * 페이징 한계와 돌파
   * 한계
     * 컬렉션을 폐치 조인하면 페이징이 불가능함
       * 컬렉션을 폐치 조인하면 일대다 조인이 발생하므로 데이터가 예측할 수 없이 증가
       * 일다대에서 일(1)을 기준으로 페이징 하는 것이 목적인데, 데이터는 다(N)를 기준으로 row가 생성됨
       * Order를 기준으로 페이징 하고 싶은데, 다(N)인 OrderItem을 조인하면 OrderItem이 기준이 됨
     * 하이버네이트는 경고 로그를 남기고 모든 DB 데이터를 읽어서 메모리에서 페이징을 시도함
   * 돌파
     * 먼저 ToOne(OneToOne, ManyToOne) 관계를 모두 폐치조인 함
       * ToOne 관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않음
     * 컬렉션은 지연 로딩으로 조회
       * 지연로딩 성능 최적화를 위해 (hibernate.default_batch_fetch_size, @BatchSize를 적용)
         * hibernate.default_batch_fetch_size 글로벌 설정
           * 100~1000 사이를 선택
           * DB에 따라 IN 절 파라미터를 1000으로 제한하기도 하기 때문
           * 애플리케이션에서 순간 부하를 어디까지 견딜 수 있는지로 결정
         * @BatchSize 개별 설정
       * 이 옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회
    * 결론
      * ToOne 관계는 폐치 조인해도 페이징에 영향을 주지 않기 때문에 폐치 조인으로 쿼리 수를 줄이고
      * 나머지는 hibernate.default_batch_fetch_size, @BatchSize를 적용


V5. JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화 여기부터
