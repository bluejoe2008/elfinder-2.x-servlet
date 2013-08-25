elfinder-2.x-servlet
====================

java servlet for elfinder-2.x
elfinder is an Open-source file manager for web, written in JavaScript using jQuery and jQuery UI
see also http://elfinder.org

in the source codes you downloaded:

# WebRoot: a normal j2ee application includes elfinder, WEB-INF, jars ...
# src: source codes for elfinder-servlet

just use following codes to tell elfinder to connect with server-side java servlet:

		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#elfinder').elfinder({
					url : 'elfinder-servlet/connector',
				});
			});
		</script>

in your web.xml, following codes should be added to enable the servlet:

	<servlet>
		<servlet-name>elfinder</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet
		</servlet-class>
	</servlet>

	<servlet-mapping>
		<servlet-name>elfinder</servlet-name>
		<url-pattern>/elfinder-servlet/*</url-pattern>
	</servlet-mapping>

yes! elfinder-2.x-servlet is developed upon SpringFramework (http://springframework.org)

a elfinder-servlet.xml configuration is required:

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd   
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd   
       http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd   
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd">

	<context:annotation-config />
	<context:component-scan base-package="cn.bluejoe.elfinder.controller" />

	<bean
		class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter" />

	<!-- find appropriate  command executor for given command-->
	<bean id="commandExecutorFactory"
		class="cn.bluejoe.elfinder.controller.executor.DefaultCommandExecutorFactory">
		<property name="classNamePattern"
			value="cn.bluejoe.elfinder.controller.executors.%sCommandExecutor" />
		<property name="map">
			<map>
			<!-- 
				<entry key="tree">
					<bean class="cn.bluejoe.elfinder.controller.executors.TreeCommandExecutor" />
				</entry>
			-->
			</map>
		</property>
	</bean>

	<!-- FsService is often retrieved from HttpRequest -->
	<!-- while a static FsService is defined here -->
	<bean id="fsServiceFactory" class="cn.bluejoe.elfinder.impl.StaticFsServiceFactory">
		<property name="fsService">
			<bean class="cn.bluejoe.elfinder.impl.DefaultFsService">
				<property name="serviceConfig">
					<bean class="cn.bluejoe.elfinder.impl.DefaultFsServiceConfig">
						<property name="tmbWidth" value="80" />
					</bean>
				</property>
				<property name="volumes">
					<list>
						<!-- two volumes are mounted here -->
						<bean class="cn.bluejoe.elfinder.localfs.LocalFsVolume">
							<property name="name" value="MyFiles" />
							<property name="rootDir" value="/tmp/a" />
						</bean>
						<bean class="cn.bluejoe.elfinder.localfs.LocalFsVolume">
							<property name="name" value="Shared" />
							<property name="rootDir" value="/tmp/b" />
						</bean>
					</list>
				</property>
				<property name="securityChecker">
					<bean class="cn.bluejoe.elfinder.impl.FsSecurityCheckerChain">
						<property name="filterMappings">
							<list>
								<bean class="cn.bluejoe.elfinder.impl.FsSecurityCheckFilterMapping">
									<property name="pattern" value="A_.*" />
									<property name="checker">
										<bean class="cn.bluejoe.elfinder.impl.FsSecurityCheckForAll">
											<property name="readable" value="true" />
											<property name="writable" value="true" />
										</bean>
									</property>
								</bean>
								<bean class="cn.bluejoe.elfinder.impl.FsSecurityCheckFilterMapping">
									<property name="pattern" value="B_.*" />
									<property name="checker">
										<bean class="cn.bluejoe.elfinder.impl.FsSecurityCheckForAll">
											<property name="readable" value="true" />
											<property name="writable" value="false" />
										</bean>
									</property>
								</bean>
							</list>
						</property>
					</bean>
				</property>
			</bean>
		</property>
	</bean>
</beans>
