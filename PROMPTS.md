PROMPT 1: Me ayudo a nombrar los comits tras explicarle que habia hehco en cada uno

Ayudame con la redacdion de manera correcta y explicativa de los commits

1. chore: estructura inicial del proyecto Spring Boot con capas
2. feat: entidades de dominio y repositories JPA
3. feat: servicios de negocio para pedidos, mesas y usuarios

PROMPT 2: Crea el sistema de Entity y de repository

PROMPT 3: Crea ek sistema de DTO

PROMPT 4: Arregla el error de importacion de jsonwebtoken añadelo al gradle
Arreglé el error agregando las dependencias de jsonwebtoken/JJWT en build.gradle:
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
Validé con:
sh gradlew compileJava

