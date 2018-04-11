what's elfinder-2.x-servlet
====================

[![GitHub issues](https://img.shields.io/github/issues/bluejoe2008/elfinder-2.x-servlet.svg)](https://github.com/bluejoe2008/elfinder-2.x-servlet/issues)
[![GitHub forks](https://img.shields.io/github/forks/bluejoe2008/elfinder-2.x-servlet.svg)](https://github.com/bluejoe2008/elfinder-2.x-servlet/network)
[![GitHub stars](https://img.shields.io/github/stars/bluejoe2008/elfinder-2.x-servlet.svg)](https://github.com/bluejoe2008/elfinder-2.x-servlet/stargazers)
[![GitHub license](https://img.shields.io/github/license/bluejoe2008/elfinder-2.x-servlet.svg)](https://github.com/bluejoe2008/elfinder-2.x-servlet/blob/master/LICENSE)

elfinder-2.x-servlet implements a java servlet for elfinder-2.x connector

elfinder is an Open-source file manager for web, written in JavaScript using jQuery and jQuery UI.
see also http://elfinder.org

<img src="https://github.com/bluejoe2008/elfinder-2.x-servlet/blob/0.9/23205811_gr0b.png?raw=true" width=500>

<img src="https://github.com/bluejoe2008/elfinder-2.x-servlet/blob/0.9/23205833_rxSV.png?raw=true" width=500>

for elfinder-1.2 users, please go to https://github.com/Studio-42/elfinder-servlet.

importing elfinder-2.x-servlet
====================
this project is released as an artifact on the central repostory

use

    <dependency>
        <groupId>com.github.bluejoe2008</groupId>
        <artifactId>elfinder-servlet-2</artifactId>
        <version>1.1</version>
        <classifier>classes</classifier>
    </dependency>

to add dependency in your pom.xml

building elfinder-2.x-servlet
====================
the source files includes:

* src/main/webapp : a normal j2ee application includes elfinder, WEB-INF...
* src/main/java: source codes for elfinder-servlet
* src/main/resources: source codes for elfinder-servlet

To build this project with maven run:

    mvn install

to run this project within a jetty container use:

    mvn jetty:run

using elfinder-2.x-servlet in your web apps
====================
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

an example elfinder-servlet.xml configuration is shown below:

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
				<property name="volumeMap">
					<!-- two volumes are mounted here -->
					<map>
						<entry key="A">
							<bean class="cn.bluejoe.elfinder.localfs.LocalFsVolume">
								<property name="name" value="MyFiles" />
								<property name="rootDir" value="/tmp/a" />
							</bean>
						</entry>
						<entry key="B">
							<bean class="cn.bluejoe.elfinder.localfs.LocalFsVolume">
								<property name="name" value="Shared" />
								<property name="rootDir" value="/tmp/b" />
							</bean>
						</entry>
					</map>
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

A ConnectorServlet is provided for people who do not use spring framework:

	<servlet>
		<servlet-name>elfinder-connector-servlet</servlet-name>
		<servlet-class>cn.bluejoe.elfinder.servlet.ConnectorServlet
		</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>elfinder-connector-servlet</servlet-name>
		<url-pattern>/elfinder-servlet/connector</url-pattern>
	</servlet-mapping>

If you want to customize behavior of ConnectorServlet(see https://github.com/bluejoe2008/elfinder-2.x-servlet/blob/0.9/src/main/java/cn/bluejoe/elfinder/servlet/ConnectorServlet.java), you may need to create a derivided servlet class based on ConnectorServlet.

features
================
* __easy to use__: just define a servlet in your web.xml, or configure the XML file in spring IOC format, and then start your web application
* __easy to import__: an artifact on the central repostory is provided, use maven to manage the dependency
* __logic file views__: a local file system is not necessary, you can define your FsService
* __easy to personalize__: different file views are allowed for different users, just provide a custom FsServiceFactory
* __easy to modify and extend__: provide your own CommandExecutors to respond new commands

Command, CommandExecutor, CommandExecutorManager
================

elfinder-2.x-servlet implements file management commands including:

*  DIM
*  DUPLICATE
*  FILE
*  GET
*  LS
*  MKDIR
*  MKFILE
*  OPEN
*  PARENT
*  PASTE
*  PUT
*  RENAME
*  RM
*  SEARCH
*  SIZE
*  TMB
*  TREE
*  UPLOAD(CHUNK supported!!!)

Each command corresponds to a CommandExecutor class, for example, the TREE command is implemented by the class TreeCommandExecutor(see https://github.com/bluejoe2008/elfinder-2.x-servlet/src/main/java/cn/bluejoe/elfinder/controller/executors/TreeCommandExecutor.java). Users can modify existing class or entend new executor class by following this naming rule.

Furthermore, this rule can even be modified via setting the commandExecutorFactory in elfinder-servlet.xml, in which default factory is DefaultCommandExecutorFactory(see https://github.com/bluejoe2008/elfinder-2.x-servlet/src/main/java/cn/bluejoe/elfinder/controller/executor/DefaultCommandExecutorFactory.java). A CommandExecutorFactory tells how to locate the command executor(TreeCommandExecutor as an example) by a given command name("TREE" as an example), it is designed as an interface:

	public interface CommandExecutorFactory
	{
		CommandExecutor get(String commandName);
	}


FsItem, FsVolume, FsService, FsServiceFactory
================
Each file is represented as a FsItem. And the root of a file is represented as a FsVolume. A FsVolume tells parent-children relations between all FsItems and implements all file operation (for example, create/delete).

A FsService may have many FsVolumes. Users can create a FsService via a FsServiceFactory:

	public interface FsServiceFactory
	{
		FsService getFileService(HttpServletRequest request, ServletContext servletContext);
	}

A simple (and stupid) StaticFsServiceFactory is provided in https://github.com/bluejoe2008/elfinder-2.x-servlet/src/main/java/cn/bluejoe/elfinder/impl/StaticFsServiceFactory.java, which always returns a fixed FsService, despite of whatever it is requested. However, sometimes a FsService should be constructed dynamically according to current Web request. For example, users may own separated file spaces in a network disk service platform, in this case, getFileService() get user principal from current request and offers him/her different file view.

Making a release
================

For a developer to make a release they need to have setup an account and with Sonatype and have a PGP key
for signing the release more details can be found at: http://central.sonatype.org/pages/apache-maven.html

Then to make a release you first tag the version and push this to github:

    mvn release:clean release:prepare

and if everything goes ok you can then release the actual artifact based on the tag:

    mvn release:perform

This will stage the artifacy on the Sonatype servers, once there it will be checked and it it's ok you can
then release it: http://central.sonatype.org/pages/releasing-the-deployment.html
