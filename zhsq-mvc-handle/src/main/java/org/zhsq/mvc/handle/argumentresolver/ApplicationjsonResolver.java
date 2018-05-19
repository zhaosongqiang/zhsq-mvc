package org.zhsq.mvc.handle.argumentresolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;
import org.springframework.asm.Type;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Content-Type Application/json
 * @author zhaosq
 * @date 2018年5月16日
 * @since 1.0
 */
public class ApplicationjsonResolver implements ArgResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationjsonResolver.class);

	private final Map<Class<?>, Map<Member, String[]>> parameterNamesCache =
			new ConcurrentHashMap<Class<?>, Map<Member, String[]>>(32);

	private static final Map<Member, String[]> NO_DEBUG_INFO_MAP = Collections.emptyMap();

	@Override
	public Object[] doResolve(FullHttpRequest request, Method handlerMethod) {
		//获取request中的json参数
		byte[] args = new byte[request.content().capacity()];
		request.content().readBytes(args);
		String jsonContent = new String (args);

		//将json解析成为Map
		Map<String, Object> paramMap =GsonBuilderHodler.GSONBUILDER.create().fromJson(jsonContent, Map.class);
		//获取paramNames
		String[] paramNames = getParamNames(handlerMethod);
		Parameter[] params = handlerMethod.getParameters();

		Object[] argsResult = new Object[paramNames.length];  

		if (paramNames.length != params.length ) {
			LOGGER.error("在解析方法：{}时获取参数名:{},参数:{},参数名和参数个数对应不上",handlerMethod, paramNames, params);
		}

		for (int i = 0; i < params.length; i ++ ) {
			//设置参数
			argsResult[i] = getParam(params[i], paramMap, paramNames[i]);
		}
		return argsResult;
	}



	private Object getParam(Parameter parameter, Map<String, Object> paramMap, String paramName) {
		String paramTypeName = parameter.getParameterizedType().getTypeName();
		Class<?> paramType = (Class<?>) parameter.getParameterizedType();
		//参数默认值解析四种类型 com.google.gson.internal.LinkedTreeMap<K, V>, Duble, Boolean, String，其余给使用者提供自定义参数解析器
		Object param = paramMap.get(paramName);

		if (param instanceof Boolean) {
			//Boolean
			return (boolean) paramMap.get(paramName);
		}

		if (param instanceof Double) {
			//Double
			Double number = (Double) paramMap.get(paramName);
			if (paramType == int.class || "java.lang.Integer".equals(paramTypeName)) {
				return number.intValue();
			}
			if (paramType == float.class || "java.lang.Float".equals(paramTypeName)) {
				return number.floatValue();
			}
			return (double) number;
		}

		if (param instanceof LinkedTreeMap) {
			//com.google.gson.internal.LinkedTreeMap<K, V> 其他对象,依赖于gson来转换对象
			LinkedTreeMap<String, Object> map = (LinkedTreeMap<String, Object>) paramMap.get(paramName);
			return GsonBuilderHodler.GSONBUILDER.create().fromJson(map.toString(), paramType);
		}

		if ("java.lang.String".equals(paramTypeName)) {
			//String
			return paramMap.get(paramName);
		}

		//TODO 给使用者自定义类型 参数类型解析器


		LOGGER.error("json参数中包含Zhsq-mvc框架无法解析的数据类型:{}",paramTypeName);
		return null;
	}



	private String[] getParamNames(Method handlerMethod) {
		Method originalMethod = BridgeMethodResolver.findBridgedMethod(handlerMethod);
		Class<?> declaringClass = originalMethod.getDeclaringClass();
		Map<Member, String[]> map = this.parameterNamesCache.get(declaringClass);
		if (map == null) {
			map = inspectClass(declaringClass);
			this.parameterNamesCache.put(declaringClass, map);
		}
		if (map == NO_DEBUG_INFO_MAP) {
			return new String[0];
		}
		return map.get(originalMethod);
	}


	private Map<Member, String[]> inspectClass(Class<?> clazz) {
		InputStream is = clazz.getResourceAsStream(ClassUtils.getClassFileName(clazz));
		if (is == null) {
			LOGGER.debug("未发现类 [{}] - 不能正确获取构造函数参数",clazz);
			return NO_DEBUG_INFO_MAP;
		}
		try {
			ClassReader classReader = new ClassReader(is);
			Map<Member, String[]> map = new ConcurrentHashMap<Member, String[]>(32);
			classReader.accept(new ParameterNameDiscoveringVisitor(clazz, map), 0);
			return map;
		}
		catch (IOException ex) {
			LOGGER.error("读取类 ["+clazz+"]时不能正确获取构造函数参数", ex);
		}
		catch (IllegalArgumentException ex) {
			LOGGER.error("ASM类加载器加载文件"+clazz+"失败，可能是由于ASM版本号不正确，请确认spring-core的版本不能低于5.0 ", ex);
		}
		finally {
			try {
				is.close();
			}
			catch (IOException ex) {
				// ignore
			}
		}
		return NO_DEBUG_INFO_MAP;
	}



	/**
	 * Helper class that inspects all methods (constructor included) and then
	 * attempts to find the parameter names for that member.
	 */
	private static class ParameterNameDiscoveringVisitor extends ClassVisitor {

		private static final String STATIC_CLASS_INIT = "<clinit>";

		private final Class<?> clazz;

		private final Map<Member, String[]> memberMap;

		public ParameterNameDiscoveringVisitor(Class<?> clazz, Map<Member, String[]> memberMap) {
			super(SpringAsmInfo.ASM_VERSION);
			this.clazz = clazz;
			this.memberMap = memberMap;
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// exclude synthetic + bridged && static class initialization
			if (!isSyntheticOrBridged(access) && !STATIC_CLASS_INIT.equals(name)) {
				return new LocalVariableTableVisitor(clazz, memberMap, name, desc, isStatic(access));
			}
			return null;
		}

		private static boolean isSyntheticOrBridged(int access) {
			return (((access & Opcodes.ACC_SYNTHETIC) | (access & Opcodes.ACC_BRIDGE)) > 0);
		}

		private static boolean isStatic(int access) {
			return ((access & Opcodes.ACC_STATIC) > 0);
		}
	}



	private static class LocalVariableTableVisitor extends MethodVisitor {

		private static final String CONSTRUCTOR = "<init>";

		private final Class<?> clazz;

		private final Map<Member, String[]> memberMap;

		private final String name;

		private final Type[] args;

		private final String[] parameterNames;

		private final boolean isStatic;

		private boolean hasLvtInfo = false;

		private final int[] lvtSlotIndex;

		public LocalVariableTableVisitor(Class<?> clazz, Map<Member, String[]> map, String name, String desc, boolean isStatic) {
			super(SpringAsmInfo.ASM_VERSION);
			this.clazz = clazz;
			this.memberMap = map;
			this.name = name;
			this.args = Type.getArgumentTypes(desc);
			this.parameterNames = new String[this.args.length];
			this.isStatic = isStatic;
			this.lvtSlotIndex = computeLvtSlotIndices(isStatic, this.args);
		}

		@Override
		public void visitLocalVariable(String name, String description, String signature, Label start, Label end, int index) {
			this.hasLvtInfo = true;
			for (int i = 0; i < this.lvtSlotIndex.length; i++) {
				if (this.lvtSlotIndex[i] == index) {
					this.parameterNames[i] = name;
				}
			}
		}

		@Override
		public void visitEnd() {
			if (this.hasLvtInfo || (this.isStatic && this.parameterNames.length == 0)) {
				this.memberMap.put(resolveMember(), this.parameterNames);
			}
		}

		private Member resolveMember() {
			ClassLoader loader = this.clazz.getClassLoader();
			Class<?>[] argTypes = new Class<?>[this.args.length];
			for (int i = 0; i < this.args.length; i++) {
				argTypes[i] = ClassUtils.resolveClassName(this.args[i].getClassName(), loader);
			}
			try {
				if (CONSTRUCTOR.equals(this.name)) {
					return this.clazz.getDeclaredConstructor(argTypes);
				}
				return this.clazz.getDeclaredMethod(this.name, argTypes);
			}
			catch (NoSuchMethodException ex) {
				throw new IllegalStateException("Method [" + this.name +
						"] was discovered in the .class file but cannot be resolved in the class object", ex);
			}
		}

		private static int[] computeLvtSlotIndices(boolean isStatic, Type[] paramTypes) {
			int[] lvtIndex = new int[paramTypes.length];
			int nextIndex = (isStatic ? 0 : 1);
			for (int i = 0; i < paramTypes.length; i++) {
				lvtIndex[i] = nextIndex;
				if (isWideType(paramTypes[i])) {
					nextIndex += 2;
				}
				else {
					nextIndex++;
				}
			}
			return lvtIndex;
		}

		private static boolean isWideType(Type aType) {
			// float is not a wide type
			return (aType == Type.LONG_TYPE || aType == Type.DOUBLE_TYPE);
		}
	}

	private static class GsonBuilderHodler{
		static final GsonBuilder GSONBUILDER = new GsonBuilder();
	}
}
