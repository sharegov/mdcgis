1. In order to use the Spring profiles configuration the VM argument needs to be passed
-Dspring.profiles.active="dev" (possible values: dev, test, prod)

2. In order to switch a pfx to a jks 
keytool -importkeystore -srckeystore somefile.pfx -srcstoretype pkcs12 -destkeystore clientcert.jks -deststoretype JKS

3. To produce a jks file
keytool -genkey -v -alias someserver.sharegov.org -keypass password -keystore keystory.jks -storepass password -keyalg "RSA" -sigalg "MD5withRSA" -keysize 2048 -validity 3650
when asked about first name and last name enter someserver.sharegov.org

The files src/main/resources/config.xml and src/test/resources/configtest.xml are not currently in this repository. I need
to figure out a way to mask values.


Test push
