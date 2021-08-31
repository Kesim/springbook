package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/*
 * JUnit �н� �׽�Ʈ
 * JUnit �� �� �׽�Ʈ �޼��带 ������ ������ �׽�Ʈ Ŭ���� ������Ʈ�� ���� �����ϴ��� Ȯ��.
 * �� �׽�Ʈ���� ���� ������ ������Ʈ�� static���� ������ ���� ������Ʈ�� ���Ͽ� Ȯ��.
 */
public class JUnitTest {
	static JUnitTest testObject;
	
	@Test
	public void test1() {
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
	
	@Test
	public void test2() {
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
	
	@Test
	public void test3() {
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
}
