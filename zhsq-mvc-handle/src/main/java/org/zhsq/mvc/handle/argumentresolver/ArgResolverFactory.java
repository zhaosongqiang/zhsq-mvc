package org.zhsq.mvc.handle.argumentresolver;

public class ArgResolverFactory {

	private static final String APPLICATION_JSON_RESOLVER = "application/json";



	public static ArgResolver createResolver(String resolve) {

		//JSR133推荐的获取单例的方法，解决了Double check的问题
		if (APPLICATION_JSON_RESOLVER.equals(resolve)) {
			return ApplicationjsonResolverHolder.APPLICATIONJSONRESOLVER;
		}
		
		//默认使用application/json方式解析
		return ApplicationjsonResolverHolder.APPLICATIONJSONRESOLVER;
	}


	private static class ApplicationjsonResolverHolder{
		static ArgResolver APPLICATIONJSONRESOLVER = new ApplicationjsonResolver();
	}

}
