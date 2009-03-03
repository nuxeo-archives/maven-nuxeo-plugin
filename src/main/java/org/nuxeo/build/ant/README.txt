This package contain ANT tasks and types to interact with maven build.
 These tasks must be used with maven-antrun-extended-plugin.

This plugin is intented to replace the assembly using ant.
To configure it you can put this in your root pom:
		<plugin>
	    <groupId>org.jvnet.maven-antrun-extended-plugin</groupId>
        <artifactId>maven-antrun-extended-plugin</artifactId>
		<version>1.37</version>
		<executions>
		<execution>
		<id>assemble-app</id>
		<phase>package</phase>
		<goals>
		  <goal>run</goal>
		</goals>
		<configuration>
		  <tasks>
		    <echo>Running ANT assembly</echo>
		    <taskdef resource="org/nuxeo/build/ant/antlib.xml"/>
			<antRun file="build.xml" optional="true" />
		  </tasks>
		</configuration>
		</execution>
		</executions>
        <dependencies>
          <!--dependency>
            <groupId>sun.jdk</groupId>
            <artifactId>tools</artifactId>
            <version>1.6</version>
            <scope>system</scope>
            <systemPath>${java.home}/../lib/tools.jar</systemPath>
          </dependency-->
		  <dependency>
		    <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-nuxeo-plugin</artifactId>
			<version>1.0.9</version>
		  </dependency>		  
         </dependencies>
       </plugin>
 
 
 And then to create a child distribution poject you need a pom.xml containing:
 in child distribution plugin you can run your build.xml file like this:
 
 <build>
   <plugins>
     ...
       <plugin>                                                                                                                                              
         <groupId>org.jvnet.maven-antrun-extended-plugin</groupId>                                                                                           
         <artifactId>maven-antrun-extended-plugin</artifactId>                                                                                               
      </plugin>
     ...
    </plugins>
 </build>
  
  And of course put your build.xml to be used for the assembly in the same directory as your pom. 


This package is adding 2 ant tasks:
antRun - which can be used to invoke ant build file from a pom.xml without loosing maven context. (maven properties):

<antRun file="build.xml" optional="true" target="some,targets" />  

profile  - which can be used as an if task to conditionally build depending on the current maven profiles:

<profile name="SQL">
   ... do something here for profile SQL
</profile>  
  
See also https://maven-antrun-extended-plugin.dev.java.net for the other available ant tasks.
