package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/*
 * JUnit 학습 테스트
 * JUnit 이 각 테스트 메서드를 수행할 때마다 테스트 클래스 오브젝트를 새로 생성하는지 확인.
 * 각 테스트에서 새로 생성된 오브젝트를 static으로 저장한 기존 오브젝트와 비교하여 확인.
 */
public class JUnitTest {
	static Set<JUnitTest> testObjects = new HashSet<>();
	
	@Test
	public void test1() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test2() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test3() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
}
