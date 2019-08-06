## Active MQ서버 설치
```shell
$ java -version

$ cd /opt/
$ sudo wget http://archive.apache.org/dist/activemq/5.15.9/apache-activemq-5.15.9-bin.tar.gz
$ sudo tar xzvf apache-activemq-5.15.9-bin.tar.gz
$ sudo ln -s apache-activemq-5.15.9 activemq
$ sudo vi /etc/systemd/system/activemq.service
```

## 사용법
브라우저로 서버에 접속한다.  
URL : http://localhost:8161/
Queues메뉴 클릭  
POSITION_INBOUND의 Operations중 Send to 클릭  
Message body에 내용을 입력하고 Send버튼을 클릭한다.  
결과 로그에 메시지 내용이 나타남.  

