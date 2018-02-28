package com.ly.fn.inf.xpro.plugin.autoconfig.annotation;

import com.ly.fn.inf.xpro.plugin.autoconfig.XproPluginRegistrar;
import com.ly.fn.inf.xpro.plugin.core.api.RunMode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.ldap.LdapDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.DeviceDelegatingViewResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.DeviceResolverAutoConfiguration;
import org.springframework.boot.autoconfigure.mobile.SitePreferenceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration;
import org.springframework.boot.autoconfigure.security.FallbackWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.boot.autoconfigure.social.LinkedInAutoConfiguration;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.encoding.FeignAcceptGzipEncodingAutoConfiguration;
import org.springframework.cloud.netflix.feign.encoding.FeignContentGzipEncodingAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ThymeleafAutoConfiguration, DispatcherServletAutoConfiguration, EmbeddedServletContainerAutoConfiguration
 * ErrorMvcAutoConfiguration, HttpEncodingAutoConfiguration, HttpMessageConvertersAutoConfiguration
 * MultipartAutoConfiguration, ServerPropertiesAutoConfiguration, WebMvcAutoConfiguration
 * WebSocketAutoConfiguration, PropertyPlaceholderAutoConfiguration.class(TODO 替换为自己的)
 * TransactionAutoConfiguration(替换为自己的), WebClientAutoConfiguration WebSocketMessagingAutoConfiguration
 * AopAutoConfiguration.class
 *
 * 默认扫描的是main方法启动类所在包以及其子包，如果Main启动类在子包内，则需要在启动类上显示使用@ComponentScan指定父包
 * @author Mitsui
 * @since 2017年12月28日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class, JndiConnectionFactoryAutoConfiguration.class, XADataSourceAutoConfiguration.class,
        JndiDataSourceAutoConfiguration.class, RabbitAutoConfiguration.class, MessageSourceAutoConfiguration.class,
        CassandraAutoConfiguration.class, BatchAutoConfiguration.class, CloudAutoConfiguration.class,
        CouchbaseAutoConfiguration.class, CassandraDataAutoConfiguration.class, CassandraRepositoriesAutoConfiguration.class,
        CouchbaseDataAutoConfiguration.class, WebServicesAutoConfiguration.class, LdapAutoConfiguration.class,
        PersistenceExceptionTranslationAutoConfiguration.class, CouchbaseRepositoriesAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class, SolrRepositoriesAutoConfiguration.class,
        RepositoryRestMvcAutoConfiguration.class, SpringDataWebAutoConfiguration.class, FreeMarkerAutoConfiguration.class,
        HypermediaAutoConfiguration.class, IntegrationAutoConfiguration.class, DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, JmsAutoConfiguration.class, JmxAutoConfiguration.class,
        ActiveMQAutoConfiguration.class, JtaAutoConfiguration.class, MailSenderAutoConfiguration.class,
        ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class,
        FlywayAutoConfiguration.class, LdapDataAutoConfiguration.class, LdapRepositoriesAutoConfiguration.class,
        GroovyTemplateAutoConfiguration.class, JerseyAutoConfiguration.class, LiquibaseAutoConfiguration.class,
        MailSenderValidatorAutoConfiguration.class, DeviceResolverAutoConfiguration.class, DeviceDelegatingViewResolverAutoConfiguration.class,
        SitePreferenceAutoConfiguration.class, EmbeddedMongoAutoConfiguration.class, MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class, MustacheAutoConfiguration.class, TwitterAutoConfiguration.class,
        ReactorAutoConfiguration.class, RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, FallbackWebSecurityAutoConfiguration.class,
        OAuth2AutoConfiguration.class, SendGridAutoConfiguration.class, SessionAutoConfiguration.class,
        SocialWebAutoConfiguration.class, FacebookAutoConfiguration.class, LinkedInAutoConfiguration.class,
        SolrAutoConfiguration.class, ThymeleafAutoConfiguration.class, TransactionAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class, Neo4jRepositoriesAutoConfiguration.class, JestAutoConfiguration.class,
        H2ConsoleAutoConfiguration.class, HypermediaAutoConfiguration.class, HazelcastAutoConfiguration.class,
        HazelcastJpaDependencyAutoConfiguration.class, ProjectInfoAutoConfiguration.class, ArtemisAutoConfiguration.class,
        JooqAutoConfiguration.class, EmbeddedLdapAutoConfiguration.class, ValidationAutoConfiguration.class,
        // feign
        FeignAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class, FeignAcceptGzipEncodingAutoConfiguration.class,
        FeignContentGzipEncodingAutoConfiguration.class
})
@EnableDiscoveryClient
//@EnableZuulProxy
@Import(XproPluginRegistrar.class)
public @interface XproPluginApplication {

    /**
     * 默认为Server模式启动，如果为Client模式则不会注册实例到eureka
     * @return
     */
    RunMode runMode() default RunMode.SERVER;
}
