elfinder-2.x-servlet
====================

elfinder-2.x-servlet provides a java servlet for elfinder-2.x (thanks for elfinder-1.2-servlet, see also https://github.com/Studio-42/elfinder-servlet)

elfinder is an Open-source file manager for web, written in JavaScript using jQuery and jQuery UI.
see also http://elfinder.org

in the zip file you downloaded:

* WebRoot: a normal j2ee application includes elfinder, WEB-INF, jars ...
* src: source codes for elfinder-servlet

just use following codes to tell elfinder to connect with server-side servlet:

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

a sample elfinder-servlet.xml configuration is shown below:

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
				<!-- settings -->
				<property name="serviceConfig">
					<bean class="cn.bluejoe.elfinder.impl.DefaultFsServiceConfig">
						<property name="tmbWidth" value="80" />
					</bean>
				</property>
				<!-- this FsService serves two volumes -->
				<property name="volumes">
					<list>
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
				<!-- define security checking rules here -->
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
