# Objetivo 
1. Crear un proyecto desde consola con el arquetipo maven-archetype-quickstart. 
Importar el proyecto en Eclipse. 
2. Crear un proyecto directamente Eclipse integrado con Maven (tipo Maven Project). Una 
vez integrado compilar, generar la documentación y empaquetar los binarios (.class) en un 
fichero .jar.  (Añadir por lo menos un fichero fuente, con una clase simple para tener 
realmente algún .class). 
3. Convertir un proyecto Eclipse tradicional en uno Maven con la opción del menú contextual 
sobre el proyecto Configure>Convert to Maven Project. 
(Observaciones: escoger unas coordenadas adecuadas para el proyecto (nombre de 
paquete, nombre de artefacto). 

## Solucion

Para crear el projecto desde la consola se utilizo el siguiente comando

```
mvn archetype:generate "-DgroupId=com.practicas.app" "-DartifactId=appdesdeconsola" "-DarchetypeArtifactId=maven-archetype-quickstart" "-DarchetypeVersion=1.4" "-DinteractiveMode=false"
```

El projecto creado desde maven se llama my app.

El projecto java convertido a maven desde Eclipse se llama ProjectoJava