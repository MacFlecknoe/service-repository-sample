#!/bin/sh

# Creating private key with given alias and password like "myAlias"/"myAliasPassword" in keystore
keytool -genkey -alias server-alias -keypass password -keystore server_store.jks -storepass password -dname "cn=server" -keyalg RSA
keytool -genkey -alias client-alias -keypass password -keystore client_store.jks -storepass password -dname "cn=client" -keyalg RSA
# Self-sign our certificate
keytool -selfcert -alias server-alias -keystore server_store.jks -storepass password -keypass password
keytool -selfcert -alias client-alias -keystore client_store.jks -storepass password -keypass password
# Export the public key from our private keystore to file named key.rsa
keytool -export -alias server-alias -file serverKey.rsa -keystore server_store.jks -storepass password
keytool -export -alias client-alias -file clientKey.rsa -keystore client_store.jks -storepass password
# Import the public key to new keystore
keytool -import -alias server-alias -file serverKey.rsa -keystore client_store.jks -storepass password
keytool -import -alias client-alias -file clientKey.rsa -keystore server_store.jks -storepass password
rm serverKey.rsa
rm clientKey.rsa

