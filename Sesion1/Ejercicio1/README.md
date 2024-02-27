Crear un script (build.xml) en un proyecto Eclipse que permita compilar, generar la 
documentación y empaquetar los binarios (.class) en un fichero .jar suponiendo que nuestro 
proyecto tiene una carpeta en el directorio raíz del proyecto src a partir de la cual se encuentran 
todos los ficheros fuente. 
Para ello se deben utilizar las tareas: javac, javadoc y jar.


Para correr este projecto se corre el comando 

```
ant clean compile doc jar run
```

desde la consola. O se corre el archivo build desde Eclipse con los comandos clean, compile, doc, jar y run seleccionados.