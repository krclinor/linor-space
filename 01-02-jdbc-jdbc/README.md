# Native JDBC
01-01-jdbc-todo프로젝트에 SingerDao인터페이스를 일반적인 JDBC프로그램으로 구현한다.  
구현한 SingerDaoImpl클래스를 분석해보자.

## SingerDao인터페이스 구현
```java
@Slf4j
@Repository
@Transactional
public class SingerDaoImpl implements SingerDao {
```
