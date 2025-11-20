<map version="1.0.1">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1760984723662" ID="ID_1769291954" MODIFIED="1760984723662" TEXT="New Mindmap">
<node CREATED="1760984733937" ID="ID_107285754" MODIFIED="1760984735573" POSITION="right" TEXT="Teste">
<node CREATED="1466802457892" ID="ID_702729061" MODIFIED="1760991872325" TEXT="resources">
<icon BUILTIN="Descriptor.grouping"/>
<node CREATED="1466802531780" FOLDED="true" ID="ID_265888708" MODIFIED="1466861480462" TEXT="META-INF">
<icon BUILTIN="File.folder"/>
<node CREATED="1466802536740" ID="ID_657409993" MODIFIED="1466861571102" TEXT="persistence.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1466802575540" ID="ID_1491681905" MODIFIED="1466802584562" TEXT="content">
<node CREATED="1760991756144" ID="ID_111521839" MODIFIED="1760991761201" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;persistence version=&quot;2.1&quot; xmlns=&quot;http://xmlns.jcp.org/xml/ns/persistence&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd&quot;&gt;&#xa;&#xa0;&#xa0;&lt;persistence-unit name=&quot;smpl&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;provider&gt;org.eclipse.persistence.jpa.PersistenceProvider&lt;/provider&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;non-jta-data-source&gt;jdbc/smpl&lt;/non-jta-data-source&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;exclude-unlisted-classes&gt;false&lt;/exclude-unlisted-classes&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;shared-cache-mode&gt;ALL&lt;/shared-cache-mode&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;properties&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.ddl-generation&quot; value=&quot;create-or-extend-tables&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.id-validation&quot; value=&quot;NONE&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.target-database&quot; value=&quot;PostgreSQL&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.deploy-on-startup&quot; value=&quot;true&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;javax.persistence.schema-generation.database.action&quot; value=&quot;create-or-extend-tables&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.ddl-generation.index-foreign-keys&quot; value=&quot;true&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/properties&gt;&#xa;&#xa0;&#xa0;&lt;/persistence-unit&gt;&#xa;&lt;/persistence&gt;"/>
</node>
</node>
<node CREATED="1466802765429" ID="ID_1143201184" MODIFIED="1466861571103" TEXT="context.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1466802769429" ID="ID_1830284073" MODIFIED="1466802771062" TEXT="content">
<node CREATED="1760991816636" ID="ID_562473978" MODIFIED="1760991818623" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;Context antiJARLocking=&quot;true&quot; path=&quot;/SmplAPI&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;ResourceLink name=&quot;jdbc/smpl&quot;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;global=&quot;jdbc/smpl&quot;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;type=&quot;javax.sql.DataSource&quot;/&gt;&#xa;&lt;/Context&gt;"/>
</node>
</node>
</node>
<node CREATED="1466802673204" FOLDED="true" ID="ID_547963767" MODIFIED="1466861489423" TEXT="WEB-INF">
<icon BUILTIN="File.folder"/>
<node CREATED="1466802681844" ID="ID_409023078" MODIFIED="1466861571098" TEXT="web.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1466802726069" ID="ID_199446105" MODIFIED="1466802744193" TEXT="content">
<node CREATED="1760991825699" ID="ID_441395952" MODIFIED="1760991828555" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;web-app version=&quot;3.0&quot; xmlns=&quot;http://java.sun.com/xml/ns/javaee&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-name&gt;MqlServlet&lt;/servlet-name&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0; &lt;servlet-class&gt;com.neoinix.smartplanner.service.MqlServlet&lt;/servlet-class&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/servlet&gt;&#xa0;&#xa0;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-mapping&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-name&gt;MqlServlet&lt;/servlet-name&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;url-pattern&gt;*.mql&lt;/url-pattern&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/servlet-mapping&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;session-config&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;session-timeout&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;30&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/session-timeout&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/session-config&gt;&#xa;&lt;/web-app&gt;"/>
</node>
</node>
<node CREATED="1466860352720" ID="ID_862001018" MODIFIED="1466861582042" TEXT="project">
<icon BUILTIN="File.folder"/>
<node CREATED="1466860358287" ID="ID_1262311064" MODIFIED="1466861617105" TEXT="context.properties">
<icon BUILTIN="File.text"/>
<node CREATED="1466861637873" ID="ID_1513465964" MODIFIED="1466861644814" TEXT="content">
<node CREATED="1466861653041" ID="ID_1260761478" MODIFIED="1466861666410" TEXT="aplication_server_host=http://localhost&#xa;aplication_server_port=9765&#xa;aplication_server_deploy_api=SmplAPI &#xa;aplication_server_deploy_app=smplapp"/>
</node>
</node>
</node>
</node>
</node>
<node CREATED="1760991907772" ID="ID_1220265454" MODIFIED="1760991907772" TEXT="resources">
<icon BUILTIN="Descriptor.grouping"/>
<node CREATED="1760991907773" MODIFIED="1760991907773" TEXT="META-INF">
<icon BUILTIN="File.folder"/>
<node CREATED="1760991907773" MODIFIED="1760991907773" TEXT="persistence.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1760991907774" MODIFIED="1760991907774" TEXT="content">
<node CREATED="1760991907774" MODIFIED="1760991907774" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;persistence version=&quot;2.1&quot; xmlns=&quot;http://xmlns.jcp.org/xml/ns/persistence&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd&quot;&gt;&#xa;&#xa0;&#xa0;&lt;persistence-unit name=&quot;smpl&quot; transaction-type=&quot;RESOURCE_LOCAL&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;provider&gt;org.eclipse.persistence.jpa.PersistenceProvider&lt;/provider&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;non-jta-data-source&gt;jdbc/smpl&lt;/non-jta-data-source&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;exclude-unlisted-classes&gt;false&lt;/exclude-unlisted-classes&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;shared-cache-mode&gt;ALL&lt;/shared-cache-mode&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;properties&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.ddl-generation&quot; value=&quot;create-or-extend-tables&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.id-validation&quot; value=&quot;NONE&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.target-database&quot; value=&quot;PostgreSQL&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.deploy-on-startup&quot; value=&quot;true&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;javax.persistence.schema-generation.database.action&quot; value=&quot;create-or-extend-tables&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;property name=&quot;eclipselink.ddl-generation.index-foreign-keys&quot; value=&quot;true&quot;/&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/properties&gt;&#xa;&#xa0;&#xa0;&lt;/persistence-unit&gt;&#xa;&lt;/persistence&gt;"/>
</node>
</node>
<node CREATED="1760991907774" MODIFIED="1760991907774" TEXT="context.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1760991907774" MODIFIED="1760991907774" TEXT="content">
<node CREATED="1760991907775" MODIFIED="1760991907775" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;Context antiJARLocking=&quot;true&quot; path=&quot;/SmplAPI&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;ResourceLink name=&quot;jdbc/smpl&quot;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;global=&quot;jdbc/smpl&quot;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;type=&quot;javax.sql.DataSource&quot;/&gt;&#xa;&lt;/Context&gt;"/>
</node>
</node>
</node>
<node CREATED="1760991907775" ID="ID_1824572662" MODIFIED="1760991907775" TEXT="WEB-INF">
<icon BUILTIN="File.folder"/>
<node CREATED="1760991907775" MODIFIED="1760991907775" TEXT="web.xml">
<icon BUILTIN="File.xml"/>
<node CREATED="1760991907775" MODIFIED="1760991907775" TEXT="content">
<node CREATED="1760991907776" MODIFIED="1760991907776" TEXT="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xa;&lt;web-app version=&quot;3.0&quot; xmlns=&quot;http://java.sun.com/xml/ns/javaee&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd&quot;&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-name&gt;MqlServlet&lt;/servlet-name&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0; &lt;servlet-class&gt;com.neoinix.smartplanner.service.MqlServlet&lt;/servlet-class&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/servlet&gt;&#xa0;&#xa0;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-mapping&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;servlet-name&gt;MqlServlet&lt;/servlet-name&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;url-pattern&gt;*.mql&lt;/url-pattern&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/servlet-mapping&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;session-config&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;session-timeout&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;30&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/session-timeout&gt;&#xa;&#xa0;&#xa0;&#xa0;&#xa0;&lt;/session-config&gt;&#xa;&lt;/web-app&gt;"/>
</node>
</node>
<node CREATED="1760991907776" MODIFIED="1760991907776" TEXT="project">
<icon BUILTIN="File.folder"/>
<node CREATED="1760991907776" MODIFIED="1760991907776" TEXT="context.properties">
<icon BUILTIN="File.text"/>
<node CREATED="1760991907776" MODIFIED="1760991907776" TEXT="content">
<node CREATED="1760991907777" MODIFIED="1760991907777" TEXT="aplication_server_host=http://localhost&#xa;aplication_server_port=9765&#xa;aplication_server_deploy_api=SmplAPI &#xa;aplication_server_deploy_app=smplapp"/>
</node>
</node>
</node>
</node>
</node>
</node>
</node>
</map>
