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
