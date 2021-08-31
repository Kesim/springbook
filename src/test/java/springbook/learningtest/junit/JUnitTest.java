package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/*
 * JUnit �н� �׽�Ʈ
 * JUnit �� �� �׽�Ʈ �޼��带 ������ ������ �׽�Ʈ Ŭ���� ������Ʈ�� ���� �����ϴ��� Ȯ��.
 * �� �׽�Ʈ���� ���� ������ ������Ʈ�� static���� ������ ���� ������Ʈ�� ���Ͽ� Ȯ��.
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
