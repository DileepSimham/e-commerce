FROM openjdk:17 
EXPOSE 8081
ADD target/e-commerce.jar e-commerce.jar
ENTRYPOINT ["java","jar","/e-commerce.jar"]