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
