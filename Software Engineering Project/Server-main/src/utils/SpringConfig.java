package utils;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ComponentScan("utils")
@EnableWebMvc
public class SpringConfig {
    public DataSource dataSource() throws URISyntaxException {
        // https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java#using-the-database_url-in-spring-with-java-configuration
        // env var DATABASE_URL must be configured for this to work.
        final String dbUrlString = System.getenv("DATABASE_URL");
        final URI dbUri = new URI(dbUrlString);

        final String username = dbUri.getUserInfo().split(":")[0];
        final String password = dbUri.getUserInfo().split(":")[1];
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriver(new org.postgresql.Driver());
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }

    @Bean
    public JdbcTemplate createJdbcTemplate() throws URISyntaxException {
        final JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(this.dataSource());
        return template;
    }
}
