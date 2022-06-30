package com.cutecatdog.cutecatdog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest 
class CutecatdogApplicationTests {
//@SpringBootTest : 통합 테스트 용도 하위의 모든 Bean을 스캔하여 로드함
//@WebMvcTest(Class명.class) 컨트롤러와 연관된 Bean이 모두 등록됨
//@MockBean 컨트롤러 내부 서비스의 요청들의 리턴 값을 보게 됨 의존성이 잡힌 객체들에 대해서 가짜 객체를 잡아서 Service 객체를 대체해서 씀
//@Import Import된 클래스는 객체를 주입 받는 방식으로 사용가능

/*
통합 테스트 : 여러 기능을 조합해서 전체 Bussinuss 로직이 제대로 동작하는지 확인

Spring Boot Test는 SpringBootApplication 을 찾아가서 모든 Bean을 로드하게 됨
무거운 작업임*/

/* 
단위 테스트 : 프로젝트에 필요한 모든 기능에 대한 테스트를 각각 진행하는 것을 의미
FIRST
Fast : 테스트 코드의 실행은 빠르게 진행되어야 함
Independent : 독립적인 테스트가 가능해야 함
Repeatable : 테스트는 매번 같은 결과를 만들어야 함
Self- Validating : 테스트 그 자체로 실행하여 결과 확인이 가능해야함
Timely : 코드가 완성되기 전 부터 테스트가 따라와야 한다는 TDD의 원칙을 담은 것 
*/


	@Test
	void contextLoads() {
	}

}
