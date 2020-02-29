
# Installation (POM)


```xml
<repository>
    <id>u2ware-mvm-repo</id>
    <url>https://raw.github.com/u2ware/u2ware.github.com/mvn-repo/</url>
</repository>

<dependency>
    <groupId>io.github.u2ware</groupId>
    <artifactId>u2ware-data-rest</artifactId>
    <version>2.1.5.1.RELEASE</version>
</dependency>
```

# Spring Data JPA 

[Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/reference/html/) 는 JPA 를 사용하는 `Spring Data Repository` 를 제공하며, 자원에 대해 operation 이 가능합니다.


```java
@Entity 
public class Foo{
    @Id @GeneratedValue
    private Long seq;
    private String name;
}
```
```java
public interface FooRepository extends PagingAndSortingRepository<Foo, Long>{
    Iterable<Foo> findByName(String name);
}
```
```java
    Foo f = new Foo("a");
    f = fooRepository.save(f); //create
    fooRepository.findById(1); //read
    fooRepository.save(f);     //update
    fooRepository.delete(f);   //delete

    fooRepository.findAll();       //search all
    fooRepository.findByName("b"); //search 
```

# Spring Data REST

[Spring Data REST](https://docs.spring.io/spring-data/rest/docs/3.1.8.RELEASE/reference/html/) 는 `Spring Data Repository` 의 리소스를 노출합니다.    
예를 들어, FooRepository 를 기반으로 Foo에 대한 REST API 와 Event Handler 를 아래와 같이 제공합니다.

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos'   -X POST -d '{"name" : ...}'          |  @HandleBeforeCreate | create     | @HandleAfterCreate |json|
|curl '/foos/1' -X GET                               |                      | read       |                    |json|
|curl '/foos/1' -X PATCH(or PUT) -d '{"name" : ...}' |  @HandleBeforeSave   | update     | @HandleAfterSave   |json|
|curl '/foos/1' -X DELETE                            |  @HandleBeforeDelete | delete     | @HandleAfterDelete |json|
|curl '/foos/'  -X GET                               |                      | search all |                    |json|  
|curl '/foos/search/findByName?name=hello' -X GET    |                      | search     |                    |json|   

```java
@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeCreate
	public void onBeforeCreate(Foo entity) { /* Before [POST] /foos */  }

	@HandleAfterCreate
	public void onAfterCreate(Foo entity) { /* After [POST] /foos */ }
	
	@HandleBeforeSave
	public void onBeforeSave(Foo entity) { /* Before [PATCH or PUT] /foos/1 */	}

	@HandleAfterSave
	public void onAfterSave(Foo entity) { /* Before [PATCH or PUT] /foos/1 */ }
	
	@HandleBeforeDelete
	public void onBeforeDelete(Foo entity) {/* Before [DELETE] /foos/1 */ }

	@HandleAfterDelete
	public void onAfterDelete(Foo entity) { /* Before [DELETE] /foos/1 */}
}
```

# U2ware Data REST

먼저, JPA 를 사용하는 `Spring Data Repository` 에  [QuerydslPredicateExecutor<T>](https://docs.spring.io/spring-data/commons/docs/2.1.8.RELEASE/api/index.html?org/springframework/data/querydsl/QuerydslPredicateExecutor.html) 또는  [JpaSpecificationExecutor<T>](https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/api/index.html?org/springframework/data/jpa/repository/JpaSpecificationExecutor.html) 확장이 필요합니다.

```java
public interface FooRepository extends PagingAndSortingRepository<Foo, Long>
					,QuerydslPredicateExecutor<Foo> //-- 
					,JpaSpecificationExecutor<Foo>  //--
{  
}
```

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 REST API 와 Event Handler 를 추가로 제공합니다.   
추가 된 API 를 호출하기 위해 요청 헤더에 `query=true` 가 반드시 요구됩니다.

|request |   before event |  operations | after event | response  |
|---|---|---|---|---|
|curl '/foos/1' -X GET -H 'query: true'                     |  [@HandleAfterRead](#handleafterread)  | read |   | json |
|curl '/foos'   -X GET -H 'query: true' -d '{"name" : ...}' |  | search |   [@HandleBeforeRead](#handlebeforeread) | json |
|curl '/foos'   -X GET -H 'query: true' -H 'csv: true' -d '{"name" : ...}' |  | search |   [@HandleBeforeRead](#handlebeforeread) | csv |


```java
@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleAfterRead
	public void onAfterRead(Foo entity) { 
		/* After [GET] /foos/1 -H 'query: true'*/ 
	}
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Object query) { 
		/* Before [GET] /foos -H 'query: true' */	
	}
}
```


# @HandleAfterRead 

 [@HandleAfterRead](src/main/java/org/springframework/data/rest/core/annotation/HandleAfterRead.java) 는 단일 자원에 대한 읽기 이벤트 이며, 이를 통해 확장 포인트를 제공합니다.
다음은 Entity 의 읽기 카운트를 1씩 증가 시키는 예시 입니다.

```java
@Component
@RepositoryEventHandler
public class ArticleHandler {
	
	private ArticleRepository articleRepository;

	@HandleAfterRead
	public void onAfterRead(Article article) { 
		article.setReadCount(article.getReadCount()+1);
		articleRepository.save(article);
	}	
}
```

# @HandleBeforeRead 

(1) `Spring Data Repository` 가 [QuerydslPredicateExecutor<T>](https://docs.spring.io/spring-data/commons/docs/2.1.8.RELEASE/api/index.html?org/springframework/data/querydsl/QuerydslPredicateExecutor.html) 를 확장한 경우, 이벤트 핸들러에  [BooleanBuilder](http://www.querydsl.com/static/querydsl/4.2.1/apidocs/index.html?com/querydsl/core/BooleanBuilder.html) 객체가 전달됩니다.

```java
public interface HelloRepository extends PagingAndSortingRepository<Hello, Long>
					,QuerydslPredicateExecutor<Hello> //-- (1)
{
}
```
```java
import com.querydsl.core.BooleanBuilder;

@Component
@RepositoryEventHandler
public class HelloHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Hello entity, Object query) {

		// query 객체 변환 
		BooleanBuilder builder = (BooleanBuilder)query; //-- (1)

	}
}
```

(2) `Spring Data Repository` 가 [JpaSpecificationExecutor<T>](https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/api/index.html?org/springframework/data/jpa/repository/JpaSpecificationExecutor.html)  를 확장한 경우, 이벤트 핸들러에   [Specification](https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/api/index.html?org/springframework/data/jpa/domain/Specification.html) 객체가 전달됩니다.

```java
public interface WorldRepository extends PagingAndSortingRepository<World, Long>
					,JpaSpecificationExecutor<World> //-- (2)
{
}
```
```java
import org.springframework.data.jpa.domain.Specification;

@Component
@RepositoryEventHandler
public class WorldHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(World entity, Object query) {

		// query 객체 변환 
		Specification spec = (Specification)query; //-- (2)

	}
}
```

# QueryDSL Predicate Builder 

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 method chain style 의 
[PredicateBuilder](src/main/java/org/springframework/data/jpa/repository/query/support/PredicateBuilder.java)
를 제공합니다.

```java
import org.springframework.data.jpa.repository.query.support.PredicateBuilder;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Object query) {

		// query 객체 변환 
		BooleanBuilder builder = (BooleanBuilder)query;

		// method chain style 의  검색 조건 생성 
		Predicate p = PredicateBuilder.of(Foo.class)   
			.where()
			.and().eq("name", entity.getName())
			.andStart()
				.andStart()
					.and().eq("age", entity.getAge())
					.or().eq("name", entity.getName())
				.andEnd()
			.andEnd()
		.build();

		// 검색 조건 추가.
		builder.and(p);  
	}
}
```


# Specification Predicate Builder 

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 method chain style 의
[PredicateBuilder](src/main/java/org/springframework/data/jpa/repository/query/PredicateBuilder.java) 를 제공합니다.

```java
import org.springframework.data.jpa.repository.query.PredicateBuilder;

@Component
@RepositoryEventHandler
public class FooHandler {
	
	@HandleBeforeRead
	public void onBeforeRead(Foo entity, Object query) {

		// query 객체 변환 
		Specification<Foo> spec = (Specification)query;

		// method chain style 의  검색 조건 생성 
		Specification<Foo> s = ((root, q, builder)->{
			return PredicateBuilder.of(root, q, builder)   
				.where()
				.and().eq("name", entity.getName())
				.andStart()
					.andStart()
						.and().eq("age", entity.getAge())
						.or().eq("name", entity.getName())
					.andEnd()
				.andEnd()
			.build();
		});

		
		// 검색 조건 추가.
		spec.and(s);  
	}
}
```

# @Formula with SpEL 


[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 는 Formula Query 안에서 SpEL 을 적용할 수 있도록 지원합니다.

```java
@Component
public class MyStatements {

	public String getStatement(){
		return "a value...";
	}
}
```


```java
@Entity
public @Data class Bar {

	@Id
	private UUID id;
	private String name;
	private Integer age;

	@Formula("(SELECT count(t.id) FROM foo t WHERE t.name = '#{myStatements.statement}')") //-> SpEL
	@JsonProperty(access=Access.READ_ONLY) 
	private Long count;
}
```

이를 위해 [u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) 에서 제공하는 [HibernateAddtionalConfiguration](src/main/java/org/springframework/data/jpa/repository/config/HibernateAddtionalConfiguration.java) 이 주입되어야 합니다.


```java
import org.springframework.data.jpa.repository.config.HibernateAddtionalConfiguration;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean //(1) Formula Query 안에서 SpEL 을 사용하기 위해 필요.
	public HibernateAddtionalConfiguration hibernateAddtionalConfiguration(EntityManagerFactory emf) {
		return new HibernateAddtionalConfiguration(emf);
	}
}
```

# Examples 

test 를 통해 더 많은 기능을 확인하십시오.



# License

[u2ware-data-rest](https://github.com/u2ware/u2ware-data-rest/) is Open Source software released under the Apache 2.0 license.

