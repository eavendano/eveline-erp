FROM openjdk:13.0.2-jdk

# ----------------------------------
# JAVA CRYPTOGRAPHY EXTENSION (JCE)
# ----------------------------------
ADD docker/resources/jre/lib/security/local_policy.jar $JAVA_HOME/lib/security/local_policy.jar
ADD docker/resources/jre/lib/security/US_export_policy.jar $JAVA_HOME/lib/security/US_export_policy.jar

# ----------------------------------
# JAVA MISSION CONTROL CREDENTIALS
# ----------------------------------
ADD docker/resources/jmc/jmxremote.access /root/jmc/jmxremote.access
ADD docker/resources/jmc/jmxremote.password /root/jmc/jmxremote.password
RUN chmod 600 /root/jmc/jmxremote.password

# ----------------------------------
# JAVA CERTIFICATE REGISTRATION
# ----------------------------------
ADD docker/resources/certificates/geotrust/GeoTrust_RSA_CA_2018.pem $JAVA_HOME/lib/security/GeoTrust_RSA_CA_2018.pem
ADD docker/resources/certificates/geotrust/GeoTrust_SSL_CA_G3.pem $JAVA_HOME/lib/security/GeoTrust_SSL_CA_G3.pem
ADD docker/resources/certificates/geotrust/GeoTrust_DV_SSL_CA_G3.pem $JAVA_HOME/lib/security/GeoTrust_DV_SSL_CA_G3.pem

RUN keytool -importcert -trustcacerts -alias "GeoTrust RSA CA 2018" -file $JAVA_HOME/lib/security/GeoTrust_RSA_CA_2018.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt
RUN keytool -importcert -trustcacerts -alias "GeoTrust SSL CA G3" -file $JAVA_HOME/lib/security/GeoTrust_SSL_CA_G3.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt
RUN keytool -importcert -trustcacerts -alias "GeoTrust DV SSL CA G3" -file $JAVA_HOME/lib/security/GeoTrust_DV_SSL_CA_G3.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt

# ----------------------------------
# APPLICATION DEPLOYMENT
# ----------------------------------
ADD target/eveline-1.3.1.jar /root/eveline.jar
CMD ["/usr/bin/sh",\
      "-c",\
      "java $JAVA_OPTIONS -jar /root/eveline.jar"]
