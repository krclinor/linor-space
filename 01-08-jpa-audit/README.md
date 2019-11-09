# Spring JPA Audit
JPA Audit(감사)기능에 대해 알아본다.
Audit은 테이블 레코드의 생성자, 생성일시, 수정자, 수정일시등의 내역을 테이블에 기록하도록 한다.  

## Spring Boot Starter를 이용한 프로젝트 생성
### 의존성 라이브러리
jpa-repository 프로젝트와 동일하게 설정한다  .    
소스 : [pom.xml](pom.xml)

### 어플리케이션 설정
jpa-repository 프로젝트와 동일하게 설정한다  .    
소스 : [application.yml](src/main/resources/application.yml)

## 엔터티 클래스 생성
### Audit용 추상클래스 생성
엔터티 클래스에 프로퍼티를 일일이 추가해도 되지만 동일한 칼럼이 여러 테이블에 존재할 수 있기 때문에 추상클래스로 작성하는 것이 편리하다.  
소스 : [Auditable.java](src/main/java/com/linor/singer/domain/Auditable.java)  
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Auditable<U> {

    @CreatedBy
    protected U createdBy;
    
    @CreatedDate
    protected LocalDateTime createdDate;
    
    @LastModifiedBy
    protected U lastModifiedBy;
    
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;
}
``` 
@MappedSuperclass로 엔터티 추상클래스임을 선언한다. 감사(Audit)가 필요한 엔터티 클래스들은 이 클래스를 상속받기만 하면 된다.   
@EntityListeners(AuditingEntityListener.class)는 
Persistence Context에서 해당 엔티티를 AuditingEntityListener.class가 감사할 수 있도록 선언한다.  
@CreatedBy, @CreatedDate, @LastModifiedBy, @LastModifiedDate은 감사관련 어노테이션이다.  

#### Singer 엔터티 클래스
소스 : [Singer.java](src/main/java/com/linor/singer/domain/Singer.java)  
가수 엔터티 클래스에 감사 기능을 적용한다.    
```java
@Entity
@Table(name="singer", uniqueConstraints = {@UniqueConstraint(name = "singer_uq_01", columnNames = {"firstName", "lastName"})})
@NamedQueries({
    @NamedQuery(name="Singer.findById",
            query="select distinct s from Singer s " +
            "left join fetch s.albums a " +
            "left join fetch s.instruments i " +
            "where s.id = :id"),
    @NamedQuery(name="Singer.findAllWithAlbum",
            query="select distinct s from Singer s \n"
                    + "left join fetch s.albums a \n"
                    + "left join fetch s.instruments i"),
    @NamedQuery(name="Singer.findByFirstName",
    query="select distinct s from Singer s \n"
            + "where s.firstName = :firstName")
})
@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
public class Singer extends Auditable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    //@Column(name="first_name")
    private String firstName;
    
    //@Column(name="last_name")
    private String lastName;
    
    //@Column(name="birth_date")
    private LocalDate birthDate;
    
    @OneToMany(mappedBy="singer", cascade=CascadeType.ALL, orphanRemoval=true)
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    private Set<Album> albums = new HashSet<>();

    @ManyToMany
    @JoinTable(name="singer_instrument", 
        joinColumns=@JoinColumn(name="singer_id"),
        inverseJoinColumns=@JoinColumn(name="instrument_id"))
    //@ToString.Exclude
    //@EqualsAndHashCode.Exclude
    private Set<Instrument> instruments = new HashSet<>();
    
    @Version
    private int version;
    
    public boolean addAlbum(Album album) {
        album.setSinger(this);
        return getAlbums().add(album);
    }
    public void reoveAlbum(Album album) {
        getAlbums().remove(album);
    }
    public boolean addInstrument(Instrument instrument) {
        return getInstruments().add(instrument);
    }
    public void reoveInstrument(Instrument instrument) {
        getInstruments().remove(instrument);
    }
}
```
@EqualsAndHashCode(callSuper=false)를 선언하여 lombok가 hashCode()생성시 부모클래스를 적용하지 않도록 한다.  
@ToString(callSuper = true)를 선언하여  toString()호출시 부모클래스의 맴버변수도 표시하도록 한다.  
Auditable클래스를 상속받은 가수 엔터티 클래스를 생성한다.  

나머지 Album, Instrument 엔터티 및 SingerSummary 도메인 클래스 jpa-repository 프로젝트와 동일하다.  
소스 : [Album.java](src/main/java/com/linor/singer/domain/Album.java)  
소스 : [Instrument.java](src/main/java/com/linor/singer/domain/Instrument.java)  
소스 : [SingerSummary.java](src/main/java/com/linor/singer/domain/SingerSummary.java)

### 엔터티별 JpaRepository인터페이스 생성 
Repository 인터페이스는 기존 jpa-repository프로젝트와 동일한다.  
소스 : [SingerRepository.java](src/main/java/com/linor/singer/repository/SingerRepository.java)  
소스 : [AlbumRepository.java](src/main/java/com/linor/singer/repository/AlbumRepository.java)  
소스 : [InstrumentRepository.java](src/main/java/com/linor/singer/repository/InstrumentRepository.java)  

### DAO인터페이스 구현클래스 생성
Repository 인터페이스는 기존 jpa-repository프로젝트와 동일한다.  

소스 : [SingerDaoImpl.java](src/main/java/com/linor/singer/repository/SingerDaoImpl.java)

### AuditorAware Bean 생성 및 설정
소스 : [JpaAuditConfig.java](src/main/java/com/linor/singer/config/JpaAuditConfig.java)  
```java
@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorAware")
public class JpaAuditConfig {
    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                return Optional.of("Linor");
            }
        };
    }

/*  
    // Spring Security 사용시 적용
    @Bean
    public AuditorAware<User> auditorAware(){
        return new AuditorAware<User>() {
            @Override
            public Optional<User> getCurrentAuditor() {
                  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                  if (authentication == null || !authentication.isAuthenticated()) {
                   return null;
                  }
                  return Optional.of((User) authentication.getPrincipal());
            }
        };
    }
*/
}
```
@Configuration을 선언하여 클래스가 설정용임을 알린다.  
@EnableJpaAuditing을 선언하여 JPA Audit을 활성화 한다.  

AuditorAware는 JPA감사 기능에서 사용자 정보를 제공한다.  
AuditorAware.getCurrentAuditor()를 구현한다.  
구현을 단순화 하기 위해 "Linor"라는 사용자명을 리턴하도록 하였다.  
Spring Security를 이용하여 구현할 경우 아래 코멘트를 해제하여 사용하면 된다.  

### 초기데이타 로딩 
개발용 시작시 초기데이타를 로딩한다.   
소스 : [AppStartupRunner.java](src/main/java/com/linor/singer/config/AppStartupRunner.java)

### Junit 테스팅
Junit으로 SingerDaoTests를 실행한다.
실행결과 audit을 적용한 경우 testFindAllByNativeQuery()가 실패로 나타남.
NativeSQL처리가 안되었다.

## 정리
Jpa Audit은 스프링에서 제공하는 기능으로 Jpa를 사용하는 경우 원하는 테이블에 Audit을 쉽게 적용할 수 있다.  
