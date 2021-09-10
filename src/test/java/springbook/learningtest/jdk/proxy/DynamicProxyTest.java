package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import springbook.learningtest.jdk.UppercaseHandler;

public class DynamicProxyTest {
	@Test
	public void simpleProxy() {
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(),
				new Class[] { Hello.class }, new UppercaseHandler(new HelloTarget()));
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UpperCaseAdvice());
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UpperCaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String result = (String) invocation.proceed();
			return result.toUpperCase();
		}
	}
	
	public static interface Hello {
		String sayHello(String name);
		
		String sayHi(String name);
		
		String sayThankYou(String name);
	}
	
	static class HelloTarget implements Hello {
		@Override
		public String sayHello(String name) {
			return "Hello " + name;
		}
		
		@Override
		public String sayHi(String name) {
			return "Hi " + name;
		}
		
		@Override
		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}
}
