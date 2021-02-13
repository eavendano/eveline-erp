package net.erp.eveline.data.generators;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import static java.lang.String.format;

public class ProductIdGenerator implements IdentifierGenerator, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(ProductIdGenerator.class);
    private String prefix;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

        Connection connection = session.connection();
        try {
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT nextval('product_id_seq')");

            if (rs.next()) {
                return prefix + format("%05d", rs.getInt(1));
            }
        } catch (Exception ex) {
            var message = format("Unable to generate providerId. | Cause: %s", ex.getMessage());
            logger.warn(message, ex);
            throw new HibernateException(message, ex);
        }
        return null;
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }
}